package avalon.usuarios.data;

import avalon.usuarios.model.pojo.ComentarioCitasMedicas;
import avalon.usuarios.model.pojo.CitaMedica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComentarioCitasMedicasRepository extends JpaRepository<ComentarioCitasMedicas, Long> {

    List<ComentarioCitasMedicas> findAllByCitaMedica(CitaMedica citaMedica);

}
