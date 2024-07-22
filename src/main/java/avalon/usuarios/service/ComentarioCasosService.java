package avalon.usuarios.service;

import avalon.usuarios.model.pojo.Caso;
import avalon.usuarios.model.pojo.CitaMedica;
import avalon.usuarios.model.pojo.ComentarioCasos;
import avalon.usuarios.model.pojo.ComentarioCitasMedicas;

import java.util.List;
import java.util.Optional;

public interface ComentarioCasosService {

    List<ComentarioCasos> getComentariosByCaso(Caso caso);
    Optional<ComentarioCasos> getComentario(Long comentarioId);
    ComentarioCasos saveComentario(ComentarioCasos comentario);
    void deleteComentario(Long comentarioId);
}
