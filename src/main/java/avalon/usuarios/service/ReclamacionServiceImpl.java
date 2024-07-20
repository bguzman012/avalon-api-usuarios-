package avalon.usuarios.service;

import avalon.usuarios.data.AseguradoraRepository;
import avalon.usuarios.data.ReclamacionRepository;
import avalon.usuarios.data.UsuarioRepository;
import avalon.usuarios.model.pojo.Aseguradora;
import avalon.usuarios.model.pojo.ClientePoliza;
import avalon.usuarios.model.pojo.Reclamacion;
import avalon.usuarios.model.request.PartiallyUpdateAseguradora;
import avalon.usuarios.model.request.PartiallyUpdateReclamacionRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
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
    public List<Reclamacion> getReclamacionesByClientePoliza(ClientePoliza clientePoliza) {
        return repository.findByClientePoliza(clientePoliza);
    }


    @Override
    public List<Reclamacion> getReclamacionByEstado(String estado) {
        return repository.findAllByEstado(estado);
    }

    @Override
    public List<Reclamacion> getReclamaciones() {
        return repository.findAll();
    }

    @Override
    public Optional<Reclamacion> getReclamacion(Long reclamacionId) {
        return repository.findById(reclamacionId);
    }

    @Override
    public Reclamacion saveReclamacion(Reclamacion reclamacion) {
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

    public Page<Reclamacion> searchReclamaciones(String busqueda, String estado, Pageable pageable, ClientePoliza clientePoliza) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Consulta principal para los resultados paginados
        CriteriaQuery<Reclamacion> query = cb.createQuery(Reclamacion.class);
        Root<Reclamacion> rRoot = query.from(Reclamacion.class);
        Join<Reclamacion, ClientePoliza> cpJoin = rRoot.join("clientePoliza");

        List<Predicate> predicates = buildPredicates(cb, rRoot, cpJoin, busqueda, estado, clientePoliza);

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        // Aplicar ordenación del pageable
        Sort.Order sortOrder = pageable.getSort().iterator().next();
        Order order = sortOrder.isAscending() ? cb.asc(rRoot.get(sortOrder.getProperty())) : cb.desc(rRoot.get(sortOrder.getProperty()));
        query.orderBy(order);

        List<Reclamacion> resultList = entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long totalRecords = countReclamaciones(busqueda, estado, clientePoliza);

        return new PageImpl<>(resultList, pageable, totalRecords);
    }

    private Long countReclamaciones(String busqueda, String estado, ClientePoliza clientePoliza) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Subconsulta para el conteo total de registros
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Reclamacion> countRoot = countQuery.from(Reclamacion.class);
        countQuery.select(cb.count(countRoot));

        Join<Reclamacion, ClientePoliza> cpJoin = countRoot.join("clientePoliza");

        List<Predicate> countPredicates = buildPredicates(cb, countRoot, cpJoin, busqueda, estado, clientePoliza);

        countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));

        try {
            return entityManager.createQuery(countQuery).getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error en la consulta de conteo", e);
        }
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<Reclamacion> rRoot, Join<Reclamacion, ClientePoliza> cpJoin, String busqueda, String estado, ClientePoliza clientePoliza) {
        List<Predicate> predicates = new ArrayList<>();

        if (busqueda != null && !busqueda.isEmpty()) {
            String likePattern = "%" + busqueda.toLowerCase() + "%";

            predicates.add(
                    cb.like(cb.lower(rRoot.get("razon")), likePattern)
            );
        }

        if (estado != null && !estado.isEmpty()) {
            predicates.add(cb.equal(rRoot.get("estado"), estado));
        }

        if (clientePoliza != null) {
            predicates.add(cb.equal(rRoot.get("clientePoliza"), clientePoliza));
        }

        return predicates;
    }
}