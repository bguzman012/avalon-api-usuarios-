package avalon.usuarios.model.auditing;

import avalon.usuarios.data.EntityAuditRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.persistence.*;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@NoArgsConstructor
public class AuditListener {

    private static final Logger logger = LoggerFactory.getLogger(AuditListener.class);

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
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void postPersistAudit(Object entity) {
        if (auditingInProgress.get()) {
            return; // Evita realizar auditoría si ya está en progreso
        }

        try {
            auditingInProgress.set(Boolean.TRUE);
            // Captura el ID después de persistir
            Long entityId = (Long) entity.getClass().getMethod("getId").invoke(entity);
            logger.info("Entity ID after persist: " + entityId);

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
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void preUpdateAudit(Object entity) {
        preUpdateRemoveAudit(entity, "UPDATE");
    }

    @PreRemove
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void preRemoveAudit(Object entity) {
        preUpdateRemoveAudit(entity, "DELETE");
    }


    public void preUpdateRemoveAudit(Object entity, String accion) {
        if (auditingInProgress.get()) {
            return;
        }

        try {
            auditingInProgress.set(Boolean.TRUE);
            Long entityId = (Long) entity.getClass().getMethod("getId").invoke(entity);

            EntityAudit audit = new EntityAudit();
            audit.setEntityId(entityId);
            audit.setEntityName(entity.getClass().getSimpleName());
            audit.setOldValue(getOldValue(entity));

            if (!accion.equals("DELETE"))
                audit.setNewValue(getNewValue(entity));
            else
                audit.setNewValue("{}");

            audit.setOperation(accion);

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
            List<EntityAudit> entitysAudit = this.entityAuditRepository.findByEntityNameAndEntityIdOrderByIdDesc(
                    entity.getClass().getSimpleName(), entityId
            );

            return entitysAudit.isEmpty() ? "{}" : entitysAudit.get(0).getNewValue();
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
            mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false); // Desactivar el formato timestamp para fechas
            mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")); // Ajusta el formato de fecha a tu necesidad
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            // Manejo de excepciones en la conversión
            return "{}";
        }
    }

}
