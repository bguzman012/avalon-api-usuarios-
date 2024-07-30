package avalon.usuarios.service;

import avalon.usuarios.data.EntityAuditRepository;
import avalon.usuarios.model.auditing.EntityAudit;
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
public class EntityAuditServiceImpl implements EntityAuditService {

    private final EntityAuditRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public EntityAuditServiceImpl(EntityAuditRepository repository) {
        this.repository = repository;
    }

    @Override
    public Page<EntityAudit> searchEntityAudits(String busquedaEntityName, String busquedaOperation, String busquedaId,
                                                String busquedaCreatedDate, String busquedaUser, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Consulta principal para los resultados paginados
        CriteriaQuery<EntityAudit> query = cb.createQuery(EntityAudit.class);
        Root<EntityAudit> root = query.from(EntityAudit.class);

        List<Predicate> predicates = buildPredicates(cb, root, busquedaEntityName);

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        // Aplicar ordenación del pageable
        Sort.Order sortOrder = pageable.getSort().iterator().next();
        Order order = sortOrder.isAscending() ? cb.asc(root.get(sortOrder.getProperty())) : cb.desc(root.get(sortOrder.getProperty()));
        query.orderBy(order);

        List<EntityAudit> entityAudits = entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long totalRecords = countEntityAudits(busquedaEntityName);

        return new PageImpl<>(entityAudits, pageable, totalRecords);
    }

    private Long countEntityAudits(String busqueda) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Subconsulta para el conteo total de registros
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<EntityAudit> countRoot = countQuery.from(EntityAudit.class);
        countQuery.select(cb.count(countRoot));

        List<Predicate> countPredicates = buildPredicates(cb, countRoot, busqueda);

        countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));

        try {
            return entityManager.createQuery(countQuery).getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error en la consulta de conteo", e);
        }
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<EntityAudit> root, String busqueda) {
        List<Predicate> predicates = new ArrayList<>();

        if (busqueda != null && !busqueda.isEmpty()) {
            String likePattern = "%" + busqueda.toLowerCase() + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("nombres")), likePattern),
                    cb.like(cb.lower(root.get("nombresDos")), likePattern),
                    cb.like(cb.lower(root.get("apellidos")), likePattern),
                    cb.like(cb.lower(root.get("apellidosDos")), likePattern),
                    cb.like(cb.lower(root.get("correoElectronico")), likePattern),
                    cb.like(cb.lower(root.get("especialidad").get("nombre")), likePattern),
                    cb.like(cb.lower(root.get("especialidad").get("descripcion")), likePattern)
            ));
        }


        return predicates;
    }
    
    @Override
    public Optional<EntityAudit> getEntityAudit(Long entityAuditId) {
        return this.repository.findById(entityAuditId);
    }
}
