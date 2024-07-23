package avalon.usuarios.service;

import avalon.usuarios.model.pojo.Emergencia;
import avalon.usuarios.model.pojo.ComentarioEmergencia;

import java.util.List;
import java.util.Optional;

public interface ComentarioEmergenciaService {

    List<ComentarioEmergencia> getComentariosByEmergencia(Emergencia emergencia);
    Optional<ComentarioEmergencia> getComentario(Long comentarioId);
    ComentarioEmergencia saveComentario(ComentarioEmergencia comentario);
    void deleteComentario(Long comentarioId);
}
