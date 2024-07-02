package avalon.usuarios.data;

import avalon.usuarios.model.pojo.Comentario;
import avalon.usuarios.model.pojo.Reclamacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

    @Query("SELECT new avalon.usuarios.model.pojo.Comentario(c.id, c.contenido, c.usuarioComenta, c.estado) " +
            "FROM Comentario c WHERE c.reclamacion = :reclamacion")
    List<Comentario> findAllByReclamacion(Reclamacion reclamacion);
}