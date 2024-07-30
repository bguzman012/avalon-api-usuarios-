package avalon.usuarios.service;

import avalon.usuarios.model.auditing.EntityAudit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface EntityAuditService {
    Page<EntityAudit> searchEntityAudits(String busquedaEntityName, String busquedaOperation, String busquedaId,
                                         String busquedaCreatedDate, String busquedaUser, Pageable pageable);

    Optional<EntityAudit> getEntityAudit(Long entityAuditId);

}
