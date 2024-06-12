package avalon.usuarios.service;

import avalon.usuarios.data.AseguradoraRepository;
import avalon.usuarios.data.MembresiaRepository;
import avalon.usuarios.data.RolRepository;
import avalon.usuarios.model.pojo.Aseguradora;
import avalon.usuarios.model.pojo.Membresia;
import avalon.usuarios.model.pojo.Rol;
import avalon.usuarios.model.request.CreateMembresiaRequest;
import avalon.usuarios.model.request.CreateRolRequest;
import avalon.usuarios.model.request.UpdateMembresiaRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public Membresia createMembresia(CreateMembresiaRequest request) {
        Aseguradora aseguradora = this.aseguradoraRepository.findById(request.getAseguradoraId()).orElse(null);
        if (aseguradora == null) return null;

        Membresia membresia = new Membresia();
        membresia.setNombres(request.getNombres());
        membresia.setDetalle(request.getDetalle());
        membresia.setEstado("A");
        membresia.setAseguradora(aseguradora);
        return repository.save(membresia);
    }

    @Override
    public List<Membresia> getMembresias() {
        return repository.findAll();
    }

    @Override
    public List<Membresia> getMembresiasByAseguradora(Long aseguradoraId) {
        Aseguradora aseguradora = this.aseguradoraRepository.findById(aseguradoraId).orElse(null);
        if (aseguradora == null) return null;

        return repository.findAllByAseguradora(aseguradora);
    }

    @Override
    public List<Membresia> getMembresiasByEstado(String estado) {
        return this.repository.findAllByEstado(estado);
    }

    @Override
    public Membresia getMembresia(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public Membresia updateMembresia(Membresia membresia, UpdateMembresiaRequest request) {
        membresia.setNombres(request.getNombres());
        membresia.setDetalle(request.getDetalle());
        membresia.setEstado(request.getEstado());
        return repository.save(membresia);
    }

    @Override
    public void deleteMembresia(Long id) {
        repository.deleteById(id);
    }
}
