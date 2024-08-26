package avalon.usuarios.service;

import avalon.usuarios.data.AseguradoraRepository;
import avalon.usuarios.data.ReclamacionRepository;
import avalon.usuarios.data.UsuarioRepository;
import avalon.usuarios.model.pojo.*;
import avalon.usuarios.model.request.PartiallyUpdateAseguradora;
import avalon.usuarios.model.request.PartiallyUpdateReclamacionRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import jakarta.persistence.criteria.Join;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ReclamacionServiceImpl implements ReclamacionService {

    private final ReclamacionRepository repository;
    private final UsuariosService usuariosService;

    private final String ROL_ADMIN = "ADM";
    private final String ROL_CLIENTE = "CLI";
    private final String ROL_ASESOR = "ASR";
    private final String ROL_AGENTE = "BRO";
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public ReclamacionServiceImpl(ReclamacionRepository repository, @Qualifier("usuariosServiceImpl") UsuariosService usuariosService) {
        this.repository = repository;
        this.usuariosService = usuariosService;
    }

    @Override
    public Optional<Reclamacion> getReclamacion(Long reclamacionId) {
        return repository.findById(reclamacionId);
    }

    @Override
    public Reclamacion saveReclamacion(Reclamacion reclamacion) {
        if (reclamacion.getCodigo() == null) {
            reclamacion.setCodigo(this.generarNuevoCodigo());
        }
        return repository.save(reclamacion);
    }

    @Override
    public Reclamacion partiallyUpdateReclamacion(PartiallyUpdateReclamacionRequest request, Long reclamacionId) {
        Reclamacion reclamacion = repository.findById(reclamacionId).orElse(null);
        if (reclamacion == null || request.getEstado() == null) return null;

        reclamacion.setEstado(request.getEstado());
        return repository.save(reclamacion);
    }

    @Override
    public void deleteReclamacion(Long reclamacionId) {
        repository.deleteById(reclamacionId);
    }

    @Override
    public String generarNuevoCodigo() {
        try {
            String ultimoCodigo = (String) entityManager.createQuery("SELECT r.codigo FROM Reclamacion r ORDER BY r.codigo DESC")
                    .setMaxResults(1)
                    .getSingleResult();

            int nuevoCodigoInt = Integer.parseInt(ultimoCodigo) + 1;
            return String.format("%07d", nuevoCodigoInt);
        } catch (NoResultException e) {
            // Si no hay resultados, se devuelve el primer código
            return "0000001";
        }
    }

    public Page<Reclamacion> searchReclamaciones(String busqueda, String estado, Pageable pageable, ClientePoliza clientePoliza, Caso caso, Usuario usuario) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Consulta principal para los resultados paginados
        CriteriaQuery<Reclamacion> query = cb.createQuery(Reclamacion.class);
        Root<Reclamacion> rRoot = query.from(Reclamacion.class);

        List<Predicate> predicates = buildPredicates(cb, rRoot, busqueda, estado, clientePoliza, caso, usuario);

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        // Aplicar ordenación del pageable
        Sort.Order sortOrder = pageable.getSort().iterator().next();
        Order order = sortOrder.isAscending() ? cb.asc(rRoot.get(sortOrder.getProperty())) : cb.desc(rRoot.get(sortOrder.getProperty()));
        query.orderBy(order);

        List<Reclamacion> resultList = entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long totalRecords = countReclamaciones(busqueda, estado, clientePoliza, caso, usuario);

        return new PageImpl<>(resultList, pageable, totalRecords);
    }

    private Long countReclamaciones(String busqueda, String estado, ClientePoliza clientePoliza, Caso caso, Usuario usuario) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Subconsulta para el conteo total de registros
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Reclamacion> countRoot = countQuery.from(Reclamacion.class);
        countQuery.select(cb.count(countRoot));

        List<Predicate> countPredicates = buildPredicates(cb, countRoot, busqueda, estado, clientePoliza, caso, usuario);

        countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));

        try {
            return entityManager.createQuery(countQuery).getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error en la consulta de conteo", e);
        }
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<Reclamacion> rRoot, String busqueda, String estado,
                                            ClientePoliza clientePoliza, Caso caso, Usuario usuario) {
        List<Predicate> predicates = new ArrayList<>();

        if (busqueda != null && !busqueda.isEmpty()) {
            String likePattern = "%" + busqueda.toLowerCase() + "%";

            predicates.add(cb.or(
                    cb.like(cb.lower(rRoot.get("razon")), likePattern)
            ));
        }

        if ((estado == null || estado.isEmpty()) && !usuario.getRol().getCodigo().equals(this.ROL_ADMIN)) {
            predicates.add(cb.notEqual(rRoot.get("estado"), "I"));
        }

        if (clientePoliza != null) {
            predicates.add(cb.equal(rRoot.get("clientePoliza"), clientePoliza));
        }

        if (caso != null) {
            predicates.add(cb.equal(rRoot.get("caso"), caso));
        }

        if (usuario.getRol().getCodigo().equals(this.ROL_ASESOR))
            predicates.add(cb.equal(rRoot.get("clientePoliza").get("asesor").get("id"), usuario.getId()));

        if (usuario.getRol().getCodigo().equals(this.ROL_AGENTE))
            predicates.add(cb.equal(rRoot.get("clientePoliza").get("agente").get("id"), usuario.getId()));

        // Si el usuario loggeado es cliente, se obtiene todos los casos del cliente,
        // o se obtiene tambien los casos de los dependientes menores de 18 años donde sea titular el cliente loggeado
        if (usuario.getRol().getCodigo().equals(this.ROL_CLIENTE)) {
            LocalDate today = LocalDate.now();
            LocalDate eighteenYearsAgo = today.minusYears(18);
            Date fechaLimite = Date.from(eighteenYearsAgo.atStartOfDay(ZoneId.systemDefault()).toInstant());

            // Predicado para cliente directo
            Predicate clienteDirecto = cb.equal(rRoot.get("clientePoliza").get("cliente").get("id"), usuario.getId());

            // Predicado para dependientes menores de 18 años
            Join<ClientePoliza, ClientePoliza> titularJoin = rRoot.join("clientePoliza", JoinType.LEFT).join("titular", JoinType.LEFT);

            Predicate titularCliente = cb.equal(titularJoin.get("cliente").get("id"), usuario.getId());
            Predicate dependienteMenorDe18 = cb.greaterThan(rRoot.get("clientePoliza").get("cliente").get("fechaNacimiento"), fechaLimite);

            predicates.add(cb.or(clienteDirecto, cb.and(titularCliente, dependienteMenorDe18)));
        }

        return predicates;
    }
}