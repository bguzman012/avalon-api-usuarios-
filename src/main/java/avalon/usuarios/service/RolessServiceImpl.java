package avalon.usuarios.service;

import avalon.usuarios.data.RolRepository;
import avalon.usuarios.model.pojo.Rol;
import avalon.usuarios.model.request.CreateRolRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolessServiceImpl implements RolesService {

    private final RolRepository repository;

    @Autowired
    public RolessServiceImpl(RolRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Rol> getRoles() {
        return repository.findAll();
    }

    @Override
    public Rol saveRol(CreateRolRequest request) {
        Rol rol;

        if (request.getId() == null) {
            rol = new Rol();
        } else {
            rol = this.repository.findById(request.getId()).orElse(null);

            if (rol == null) return null;
        }
        rol.setNombre(request.getNombre());
        rol.setCodigo(request.getCodigo());

        return this.repository.save(rol);
    }

    @Override
    public Boolean deleteRol(Long rolId) {
        Rol rol = this.repository.findById(rolId).orElse(null);

        if (rol == null)
            return Boolean.FALSE;

        this.repository.delete(rol);
        return Boolean.TRUE;
    }

}
