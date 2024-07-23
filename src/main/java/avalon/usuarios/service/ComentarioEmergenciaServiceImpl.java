package avalon.usuarios.service;

import avalon.usuarios.data.ComentarioEmergenciaRepository;
import avalon.usuarios.model.pojo.Emergencia;
import avalon.usuarios.model.pojo.ComentarioEmergencia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ComentarioEmergenciaServiceImpl implements ComentarioEmergenciaService {

    private final ComentarioEmergenciaRepository repository;

    @Autowired
    public ComentarioEmergenciaServiceImpl(ComentarioEmergenciaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ComentarioEmergencia> getComentariosByEmergencia(Emergencia emergencia) {
        return repository.findAllByEmergencia(emergencia);
    }

    @Override
    public Optional<ComentarioEmergencia> getComentario(Long comentarioId) {
        return repository.findById(comentarioId);
    }

    @Override
    public ComentarioEmergencia saveComentario(ComentarioEmergencia comentario) {
        return repository.save(comentario);
    }

    @Override
    public void deleteComentario(Long comentarioId) {
        repository.deleteById(comentarioId);
    }
}
