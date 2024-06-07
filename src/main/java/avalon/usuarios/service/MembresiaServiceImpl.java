package avalon.usuarios.service;

import avalon.usuarios.data.MembresiaRepository;
import avalon.usuarios.data.RolRepository;
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

    private final MembresiaRepository repository;

    @Autowired
    public MembresiaServiceImpl(MembresiaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Membresia createMembresia(CreateMembresiaRequest request) {
        Membresia membresia = new Membresia();
        membresia.setNombres(request.getNombres());
        membresia.setDetalle(request.getDetalle());
        membresia.setEstado(request.getEstado());
        return repository.save(membresia);
    }

    @Override
    public List<Membresia> getMembresias() {
        return repository.findAll();
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
