package avalon.usuarios.service;

import avalon.usuarios.data.AseguradoraRepository;
import avalon.usuarios.data.ReclamacionRepository;
import avalon.usuarios.data.UsuarioRepository;
import avalon.usuarios.model.pojo.Aseguradora;
import avalon.usuarios.model.pojo.Caso;
import avalon.usuarios.model.pojo.ClientePoliza;
import avalon.usuarios.model.pojo.Reclamacion;
import avalon.usuarios.model.request.PartiallyUpdateAseguradora;
import avalon.usuarios.model.request.PartiallyUpdateReclamacionRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import jakarta.persistence.criteria.Join;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReclamacionServiceImpl implements ReclamacionService {

    private final ReclamacionRepository repository;
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public ReclamacionServiceImpl(ReclamacionRepository repository) {
        this.repository = repository;
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
        if (reclamacion == null) return null;

        if (request.getEstado() != null)
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

    public Page<Reclamacion> searchReclamaciones(String busqueda, String estado, Pageable pageable, ClientePoliza clientePoliza, Caso caso) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Consulta principal para los resultados paginados
        CriteriaQuery<Reclamacion> query = cb.createQuery(Reclamacion.class);
        Root<Reclamacion> rRoot = query.from(Reclamacion.class);

        List<Predicate> predicates = buildPredicates(cb, rRoot, busqueda, estado, clientePoliza, caso);

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        // Aplicar ordenación del pageable
        Sort.Order sortOrder = pageable.getSort().iterator().next();
        Order order = sortOrder.isAscending() ? cb.asc(rRoot.get(sortOrder.getProperty())) : cb.desc(rRoot.get(sortOrder.getProperty()));
        query.orderBy(order);

        List<Reclamacion> resultList = entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long totalRecords = countReclamaciones(busqueda, estado, clientePoliza, caso);

        return new PageImpl<>(resultList, pageable, totalRecords);
    }

    private Long countReclamaciones(String busqueda, String estado, ClientePoliza clientePoliza, Caso caso) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Subconsulta para el conteo total de registros
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Reclamacion> countRoot = countQuery.from(Reclamacion.class);
        countQuery.select(cb.count(countRoot));

        List<Predicate> countPredicates = buildPredicates(cb, countRoot, busqueda, estado, clientePoliza, caso);

        countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));

        try {
            return entityManager.createQuery(countQuery).getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error en la consulta de conteo", e);
        }
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<Reclamacion> rRoot, String busqueda, String estado,
                                            ClientePoliza clientePoliza, Caso caso) {
        List<Predicate> predicates = new ArrayList<>();

        if (busqueda != null && !busqueda.isEmpty()) {
            String likePattern = "%" + busqueda.toLowerCase() + "%";

            predicates.add(cb.or(
                    cb.like(cb.lower(rRoot.get("razon")), likePattern)
            ));
        }

        if (estado != null && !estado.isEmpty()) {
            predicates.add(cb.equal(rRoot.get("estado"), estado));
        }

        if (clientePoliza != null) {
            predicates.add(cb.equal(rRoot.get("clientePoliza"), clientePoliza));
        }

        if (caso != null) {
            predicates.add(cb.equal(rRoot.get("caso"), caso));
        }

        return predicates;
    }
}