package avalon.usuarios.data;

import avalon.usuarios.model.auditing.EntityAudit;
import avalon.usuarios.model.pojo.Comentario;
import avalon.usuarios.model.pojo.Reclamacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface EntityAuditRepository extends JpaRepository<EntityAudit, Long> {

    List<EntityAudit> findByEntityNameAndEntityIdOrderByIdDesc(String entityName, Long entityId);
}