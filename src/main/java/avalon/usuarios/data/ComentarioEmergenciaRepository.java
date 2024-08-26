package avalon.usuarios.data;

import avalon.usuarios.model.pojo.Emergencia;
import avalon.usuarios.model.pojo.ComentarioEmergencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComentarioEmergenciaRepository extends JpaRepository<ComentarioEmergencia, Long> {

    List<ComentarioEmergencia> findAllByEmergencia(Emergencia emergencia);
}
