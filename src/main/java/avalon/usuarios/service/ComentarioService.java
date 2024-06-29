package avalon.usuarios.service;

import avalon.usuarios.model.pojo.Comentario;
import avalon.usuarios.model.pojo.Reclamacion;
import avalon.usuarios.model.request.ComentarioRequest;
import avalon.usuarios.model.request.PartiallyUpdateReclamacionRequest;

import java.util.List;
import java.util.Optional;

public interface ComentarioService {

    List<Comentario> getComentariosByReclamacion(Reclamacion reclamacion);
    Optional<Comentario> getComentario(Long comentarioId);
    Comentario saveComentario(Comentario comentario);
    void deleteComentario(Long comentarioId);
}