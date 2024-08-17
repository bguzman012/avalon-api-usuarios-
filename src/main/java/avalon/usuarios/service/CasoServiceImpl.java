package avalon.usuarios.service;

import avalon.usuarios.data.CasoRepository;
import avalon.usuarios.model.pojo.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
public class CasoServiceImpl implements CasoService {

    private final CasoRepository repository;
    private final String ROL_CLIENTE = "CLI";
    private final String ROL_ASESOR = "ASR";
    private final String ROL_AGENTE = "BRO";

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public CasoServiceImpl(CasoRepository repository) {
        this.repository = repository;
    }


    @Override
    public Page<Caso> searchCasos(String busqueda, Pageable pageable, ClientePoliza clientePoliza, Usuario usuario) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Consulta principal para los resultados paginados
        CriteriaQuery<Caso> query = cb.createQuery(Caso.class);
        Root<Caso> root = query.from(Caso.class);

        List<Predicate> predicates = buildPredicates(cb, root, busqueda, clientePoliza, usuario);

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        Sort.Order sortOrder = pageable.getSort().iterator().next();
        Order order = sortOrder.isAscending() ? cb.asc(root.get(sortOrder.getProperty())) : cb.desc(root.get(sortOrder.getProperty()));
        query.orderBy(order);

        List<Caso> resultList = entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long totalRecords = countCasos(busqueda, clientePoliza, usuario);
        return new PageImpl<>(resultList, pageable, totalRecords);
    }

    private Long countCasos(String busqueda,  ClientePoliza clientePoliza, Usuario usuario) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Subconsulta para el conteo total de registros
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Caso> countRoot = countQuery.from(Caso.class);
        countQuery.select(cb.count(countRoot));

        List<Predicate> countPredicates = buildPredicates(cb, countRoot, busqueda, clientePoliza, usuario);

        countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));

        try {
            return entityManager.createQuery(countQuery).getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error en la consulta de conteo", e);
        }
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb,
                                            Root<Caso> root,
                                            String busqueda,
                                            ClientePoliza clientePoliza,
                                            Usuario usuario) {
        List<Predicate> predicates = new ArrayList<>();

        if (busqueda != null && !busqueda.isEmpty()) {
            String likePattern = "%" + busqueda.toLowerCase() + "%";

            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("codigo")), likePattern),

                    cb.like(cb.lower(root.get("clientePoliza").get("cliente").get("nombreUsuario")), likePattern),
                    cb.like(cb.lower(root.get("clientePoliza").get("cliente").get("nombres")), likePattern),
                    cb.like(cb.lower(root.get("clientePoliza").get("cliente").get("apellidos")), likePattern),

                    cb.like(cb.lower(root.get("clientePoliza").get("poliza").get("nombre")), likePattern)
            ));
        }
//
        if (clientePoliza != null) {
            predicates.add(cb.equal(root.get("clientePoliza").get("id"), clientePoliza.getId()));
        }

        if (usuario.getRol().getCodigo().equals(this.ROL_ASESOR))
            predicates.add(cb.equal(root.get("clientePoliza").get("asesor").get("id"), usuario.getId()));

        if (usuario.getRol().getCodigo().equals(this.ROL_AGENTE))
            predicates.add(cb.equal(root.get("clientePoliza").get("agente").get("id"), usuario.getId()));

        // Si el usuario loggeado es cliente, se obtiene todos los casos del cliente,
        // o se obtiene tambien los casos de los dependientes menores de 18 años donde sea titular el cliente loggeado
        if (usuario.getRol().getCodigo().equals(this.ROL_CLIENTE)) {
            LocalDate today = LocalDate.now();
            LocalDate eighteenYearsAgo = today.minusYears(18);
            Date fechaLimite = Date.from(eighteenYearsAgo.atStartOfDay(ZoneId.systemDefault()).toInstant());

            // Predicado para cliente directo
            Predicate clienteDirecto = cb.equal(root.get("clientePoliza").get("cliente").get("id"), usuario.getId());

            // Predicado para dependientes menores de 18 años
            Join<ClientePoliza, ClientePoliza> titularJoin = root.join("clientePoliza", JoinType.LEFT).join("titular", JoinType.LEFT);

            Predicate titularCliente = cb.equal(titularJoin.get("cliente").get("id"), usuario.getId());
            Predicate dependienteMenorDe18 = cb.greaterThan(root.get("clientePoliza").get("cliente").get("fechaNacimiento"), fechaLimite);

            predicates.add(cb.or(clienteDirecto, cb.and(titularCliente, dependienteMenorDe18)));
        }

        return predicates;
    }

    @Override
    public Optional<Caso> getCaso(Long casoId) {
        return repository.findById(casoId);
    }

    @Override
    public Caso saveCaso(Caso caso) {
        if (caso.getCodigo() == null) {
            caso.setCodigo(this.generarNuevoCodigo());
        }
        return repository.save(caso);
    }

    @Override
    public void deleteCaso(Long casoId) {
        repository.deleteById(casoId);
    }

    public String generarNuevoCodigo() {
        try {
            String ultimoCodigo = (String) entityManager.createQuery("SELECT c.codigo FROM Caso c ORDER BY c.codigo DESC")
                    .setMaxResults(1)
                    .getSingleResult();

            int nuevoCodigoInt = Integer.parseInt(ultimoCodigo) + 1;
            return String.format("%07d", nuevoCodigoInt);
        } catch (NoResultException e) {
            // Si no hay resultados, se devuelve el primer código
            return "0000001";
        }
    }

}
