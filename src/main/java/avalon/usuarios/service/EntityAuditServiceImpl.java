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

        List<Predicate> predicates = buildPredicates(cb, root, busquedaEntityName, busquedaOperation, busquedaId, busquedaCreatedDate, busquedaUser);

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        // Aplicar ordenación del pageable
        Sort.Order sortOrder = pageable.getSort().iterator().next();
        Order order = sortOrder.isAscending() ? cb.asc(root.get(sortOrder.getProperty())) : cb.desc(root.get(sortOrder.getProperty()));
        query.orderBy(order);

        List<EntityAudit> entityAudits = entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long totalRecords = countEntityAudits(busquedaEntityName, busquedaOperation, busquedaId, busquedaCreatedDate, busquedaUser);

        return new PageImpl<>(entityAudits, pageable, totalRecords);
    }

    private Long countEntityAudits(String busquedaEntityName, String busquedaOperation, String busquedaId,
                                   String busquedaCreatedDate, String busquedaUser) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Subconsulta para el conteo total de registros
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<EntityAudit> countRoot = countQuery.from(EntityAudit.class);
        countQuery.select(cb.count(countRoot));

        List<Predicate> countPredicates = buildPredicates(cb, countRoot, busquedaEntityName, busquedaOperation, busquedaId, busquedaCreatedDate, busquedaUser);

        countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));

        try {
            return entityManager.createQuery(countQuery).getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error en la consulta de conteo", e);
        }
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<EntityAudit> root, String busquedaEntityName,
                                            String busquedaOperation, String busquedaId, String busquedaCreatedDate,
                                            String busquedaUser) {

        List<Predicate> predicates = new ArrayList<>();


        String likePatternName = "%" + busquedaEntityName.toLowerCase() + "%";
        String likePatternOperation = "%" + busquedaOperation.toLowerCase() + "%";
        String likePatternCreatedDate = "%" + busquedaCreatedDate.toLowerCase() + "%";
        String likePatternUser = "%" + busquedaUser.toLowerCase() + "%";

        predicates.add(cb.and(
                cb.like(cb.lower(root.get("entityName")), likePatternName),
                cb.like(cb.lower(root.get("operation")), likePatternOperation),
                cb.like(cb.function("TO_CHAR", String.class, root.get("createdDate"), cb.literal("yyyy-MM-dd")), likePatternCreatedDate),
                cb.like(cb.lower(root.get("createdBy")), likePatternUser)
        ));

        if (busquedaId != null && !busquedaId.isEmpty()) {
            predicates.add(
                    cb.equal(root.get("entityId"), Long.valueOf(busquedaId))
            );
        }


        return predicates;
    }

    @Override
    public Optional<EntityAudit> getEntityAudit(Long entityAuditId) {
        return this.repository.findById(entityAuditId);
    }
}
