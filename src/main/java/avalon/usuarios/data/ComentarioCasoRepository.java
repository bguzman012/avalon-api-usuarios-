package avalon.usuarios.data;

import avalon.usuarios.model.pojo.Caso;
import avalon.usuarios.model.pojo.CitaMedica;
import avalon.usuarios.model.pojo.ComentarioCasos;
import avalon.usuarios.model.pojo.ComentarioCitasMedicas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComentarioCasoRepository extends JpaRepository<ComentarioCasos, Long> {

    @Query("SELECT new avalon.usuarios.model.pojo.ComentarioCasos(c.id, c.contenido, c.usuarioComenta, c.estado) " +
            "FROM ComentarioCasos c WHERE c.caso = :caso")
    List<ComentarioCasos> findAllByCaso(Caso caso);
}
