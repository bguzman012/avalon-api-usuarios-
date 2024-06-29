package avalon.usuarios.service;

import avalon.usuarios.data.ComentarioRepository;
import avalon.usuarios.data.ReclamacionRepository;
import avalon.usuarios.model.pojo.Comentario;
import avalon.usuarios.model.pojo.Reclamacion;
import avalon.usuarios.model.request.ComentarioRequest;
import avalon.usuarios.model.request.PartiallyUpdateReclamacionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ComentarioServiceImpl implements ComentarioService {

    private final ComentarioRepository repository;

    @Autowired
    public ComentarioServiceImpl(ComentarioRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Comentario> getComentariosByReclamacion(Reclamacion reclamacion) {
        return repository.findAllByReclamacion(reclamacion);
    }

    @Override
    public Optional<Comentario> getComentario(Long comentarioId) {
        return repository.findById(comentarioId);
    }

    @Override
    public Comentario saveComentario(Comentario comentario) {
        return repository.save(comentario);
    }

    @Override
    public void deleteComentario(Long comentarioId) {
        repository.deleteById(comentarioId);
    }
}