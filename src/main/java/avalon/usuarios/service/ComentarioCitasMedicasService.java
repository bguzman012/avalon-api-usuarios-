package avalon.usuarios.service;

import avalon.usuarios.model.pojo.ComentarioCitasMedicas;
import avalon.usuarios.model.pojo.CitaMedica;

import java.util.List;
import java.util.Optional;

public interface ComentarioCitasMedicasService {

    List<ComentarioCitasMedicas> getComentariosByCitaMedica(CitaMedica citaMedica);
    Optional<ComentarioCitasMedicas> getComentario(Long comentarioId);
    ComentarioCitasMedicas saveComentario(ComentarioCitasMedicas comentario);
    void deleteComentario(Long comentarioId);
}
