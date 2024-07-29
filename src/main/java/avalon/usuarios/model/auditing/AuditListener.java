package avalon.usuarios.model.auditing;

import avalon.usuarios.data.EntityAuditRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;

//@EntityListeners(AuditingEntityListener.class)
@Component
@NoArgsConstructor
public class AuditListener {

    private EntityAuditRepository entityAuditRepository;
    private EntityManager entityManager;

    @Autowired
    public void setEntityAuditRepository(@Lazy EntityAuditRepository entityAuditRepository) {
        this.entityAuditRepository = entityAuditRepository;
    }

    @Autowired
    public void setEntityManager(@Lazy EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    private static final ThreadLocal<Boolean> auditingInProgress = ThreadLocal.withInitial(() -> Boolean.FALSE);

    @PostPersist
    public void postPersistAudit(Object entity) {
        if (auditingInProgress.get()) {
            return; // Evita realizar auditoría si ya está en progreso
        }

        try {
            auditingInProgress.set(Boolean.TRUE);
            // Captura el ID después de persistir
            Long entityId = (Long) entity.getClass().getMethod("getId").invoke(entity);

            EntityAudit audit = new EntityAudit();
            audit.setEntityId(entityId);
            audit.setEntityName(entity.getClass().getSimpleName());
            audit.setOldValue("{}"); // No hay valor anterior para una nueva entidad
            audit.setNewValue(convertToJson(entity));
            audit.setOperation("CREATE");

            entityAuditRepository.save(audit);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            // Manejo de excepciones específicas
        } catch (Exception e) {
            // Manejo de excepciones generales
        } finally {
            auditingInProgress.remove(); // Asegura que se reinicie el estado después de la auditoría
        }
    }

    @PreUpdate
    @PreRemove
    public void preUpdateRemoveAudit(Object entity) {
        if (auditingInProgress.get()) {
            return; // Evita realizar auditoría si ya está en progreso
        }

        try {
            auditingInProgress.set(Boolean.TRUE);
            String operation = getOperationType(entity);
            Long entityId = (Long) entity.getClass().getMethod("getId").invoke(entity);

            EntityAudit audit = new EntityAudit();
            audit.setEntityId(entityId);
            audit.setEntityName(entity.getClass().getSimpleName());
            audit.setOldValue(getOldValue(entity));
            audit.setNewValue(getNewValue(entity));
            audit.setOperation(operation);

            entityAuditRepository.save(audit);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            // Manejo de excepciones específicas
        } catch (Exception e) {
            // Manejo de excepciones generales
        } finally {
            auditingInProgress.remove(); // Asegura que se reinicie el estado después de la auditoría
        }
    }

    private String getOldValue(Object entity) {
        try {
            Long entityId = (Long) entity.getClass().getMethod("getId").invoke(entity);
            Object oldEntity = this.entityAuditRepository.findByEntityNameAndEntityIdOrderByIdDesc(
                    entity.getClass().getSimpleName(), entityId
            );
            return convertToJson(oldEntity);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            // Manejo de excepciones específicas
            return "{}"; // Valor por defecto en caso de error
        }
    }

    private String getNewValue(Object entity) {
        return convertToJson(entity);
    }

    private String convertToJson(Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            // Manejo de excepciones en la conversión
            return "{}";
        }
    }

    private String getOperationType(Object entity) {
        if (entityManager.contains(entity)) {
            return "UPDATE";
        } else if (entityManager.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity) == null) {
            return "CREATE";
        } else {
            return "DELETE";
        }
    }

}
