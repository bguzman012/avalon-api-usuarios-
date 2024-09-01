package avalon.usuarios.service;

import avalon.usuarios.data.EstadoRepository;
import avalon.usuarios.data.PaisRepository;
import avalon.usuarios.model.pojo.Estado;
import avalon.usuarios.model.pojo.Pais;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaisServiceImpl implements PaisService {

    private final PaisRepository repository;

    @Autowired
    public PaisServiceImpl(PaisRepository repository) {
        this.repository = repository;
    }
    @Override
    public List<Pais> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Pais> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Optional<Pais> findByNombre(String nombre) {
        return repository.findByNombreContainingIgnoreCase(nombre);
    }
}
