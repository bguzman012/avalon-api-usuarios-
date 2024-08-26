package avalon.usuarios.service;

import avalon.usuarios.data.ComentarioCitasMedicasRepository;
import avalon.usuarios.model.pojo.ComentarioCitasMedicas;
import avalon.usuarios.model.pojo.CitaMedica;
import avalon.usuarios.model.request.PartiallyUpdateCitaMedicaRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ComentarioCitasMedicasServiceImpl implements ComentarioCitasMedicasService {

    private final ComentarioCitasMedicasRepository repository;

    @Autowired
    public ComentarioCitasMedicasServiceImpl(ComentarioCitasMedicasRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ComentarioCitasMedicas> getComentariosByCitaMedica(CitaMedica citaMedica) {
        return repository.findAllByCitaMedica(citaMedica);
    }

    @Override
    public Optional<ComentarioCitasMedicas> getComentario(Long comentarioId) {
        return repository.findById(comentarioId);
    }

    @Override
    public ComentarioCitasMedicas saveComentario(ComentarioCitasMedicas comentario) {
        return repository.save(comentario);
    }

    @Override
    public void deleteComentario(Long comentarioId) {
        repository.deleteById(comentarioId);
    }
}
