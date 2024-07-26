package avalon.usuarios.service;

import avalon.usuarios.data.EntityAuditRepository;
import avalon.usuarios.model.auditing.EntityAudit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditService {

    @Autowired
    private EntityAuditRepository entityAuditRepository;

    @Autowired
    private AuditorAware<String> auditorAware;

    public void logAudit(String entityName, Long entityId, String operation, String oldValue, String newValue) {
        try {
            String auditor = auditorAware.getCurrentAuditor().orElse("unknown");
            EntityAudit audit = new EntityAudit();
            audit.setEntityId(entityId);
            audit.setEntityName(entityName);
            audit.setModifiedBy(auditor);
            audit.setModifiedDate(LocalDateTime.now());
            audit.setOperation(operation);
            audit.setOldValue(oldValue);
            audit.setNewValue(newValue);
            entityAuditRepository.save(audit);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
