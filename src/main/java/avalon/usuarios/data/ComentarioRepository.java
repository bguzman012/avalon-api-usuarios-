package avalon.usuarios.data;

import avalon.usuarios.model.pojo.Comentario;
import avalon.usuarios.model.pojo.Reclamacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    List<Comentario> findAllByReclamacion(Reclamacion reclamacion);
}