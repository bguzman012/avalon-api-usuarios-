package avalon.usuarios.data;

import avalon.usuarios.model.pojo.Emergencia;
import avalon.usuarios.model.pojo.ComentarioEmergencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComentarioEmergenciaRepository extends JpaRepository<ComentarioEmergencia, Long> {

    @Query("SELECT new avalon.usuarios.model.pojo.ComentarioEmergencia(e.id, e.contenido, e.usuarioComenta, e.estado) " +
            "FROM ComentarioEmergencia e WHERE e.emergencia = :emergencia")
    List<ComentarioEmergencia> findAllByEmergencia(Emergencia emergencia);
}
