package avalon.usuarios.config;

import avalon.usuarios.data.EntityAuditRepository;
import avalon.usuarios.model.auditing.EntityAudit;
import avalon.usuarios.service.AuditService;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Optional;

@Configuration
public class AuditListener {

    @Autowired
    private EntityAuditRepository entityAuditRepository;

    @Autowired
    private AuditService auditService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @PrePersist
    @PreUpdate
    @PreRemove
    public void captureAuditData(Object entity) {
        try {
            String entityName = entity.getClass().getSimpleName();
            Long entityId = getEntityId(entity);
            String operation = getOperation(entity);
            String oldValue = getOldValue(entity, entityId);
            String newValue = getNewValue(entity);

            auditService.logAudit(entityName, entityId, operation, oldValue, newValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Long getEntityId(Object entity) {
        try {
            return (Long) entity.getClass().getMethod("getId").invoke(entity);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getOperation(Object entity) {
        if (entity.getClass().isAnnotationPresent(PrePersist.class)) {
            return "INSERT";
        } else if (entity.getClass().isAnnotationPresent(PreUpdate.class)) {
            return "UPDATE";
        } else if (entity.getClass().isAnnotationPresent(PreRemove.class)) {
            return "DELETE";
        }
        return "UNKNOWN";
    }

    private String getOldValue(Object entity, Long entityId) {
        try {
            if (entityId != null) {
                Optional<?> oldEntity = entityAuditRepository.findById(entityId);
                if (oldEntity.isPresent()) {
                    return objectMapper.writeValueAsString(oldEntity.get());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "{}";
    }

    private String getNewValue(Object entity) {
        try {
            return objectMapper.writeValueAsString(entity);
        } catch (Exception e) {
            e.printStackTrace();
            return "{}";
        }
    }
}
