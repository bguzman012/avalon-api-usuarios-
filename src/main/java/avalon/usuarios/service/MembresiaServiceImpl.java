package avalon.usuarios.service;

import avalon.usuarios.data.AseguradoraRepository;
import avalon.usuarios.data.MembresiaRepository;
import avalon.usuarios.model.pojo.Aseguradora;
import avalon.usuarios.model.pojo.Membresia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MembresiaServiceImpl implements MembresiaService {

    @Autowired
    private AseguradoraRepository aseguradoraRepository;
    private final MembresiaRepository repository;

    @Autowired
    public MembresiaServiceImpl(MembresiaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Membresia saveMembresia(Membresia membresia) {
        return repository.save(membresia);
    }

    @Override
    public List<Membresia> getMembresias() {
        return repository.findAll();
    }

    @Override
    public List<Membresia> getMembresiasByEstado(String estado) {
        return this.repository.findAllByEstado(estado);
    }

    @Override
    public Page<Membresia> searchMembresias(String estado, String busqueda, Pageable pageable) {
        return this.repository.searchMembresias(estado, busqueda, pageable);
    }

    @Override
    public Optional<Membresia> getMembresia(Long id) {
        return repository.findById(id);
    }

    @Override
    public Optional<Membresia> getMembresiaByName(String membresiaName) {
        return this.repository.findByNombresContainingIgnoreCase(membresiaName);
    }

    @Override
    public void deleteMembresia(Long id) {
        repository.deleteById(id);
    }
}
