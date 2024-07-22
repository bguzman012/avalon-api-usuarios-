package avalon.usuarios.service;

import avalon.usuarios.data.ComentarioCasoRepository;
import avalon.usuarios.model.pojo.Caso;
import avalon.usuarios.model.pojo.ComentarioCasos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ComentarioCasosServiceImpl implements ComentarioCasosService {

    private final ComentarioCasoRepository repository;

    @Autowired
    public ComentarioCasosServiceImpl(ComentarioCasoRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ComentarioCasos> getComentariosByCaso(Caso caso) {
        return repository.findAllByCaso(caso);
    }

    @Override
    public Optional<ComentarioCasos> getComentario(Long comentarioId) {
        return repository.findById(comentarioId);
    }

    @Override
    public ComentarioCasos saveComentario(ComentarioCasos comentario) {
        return repository.save(comentario);
    }

    @Override
    public void deleteComentario(Long comentarioId) {
        repository.deleteById(comentarioId);
    }
}
