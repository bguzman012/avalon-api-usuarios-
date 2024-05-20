package avalon.usuarios.service;

import avalon.usuarios.model.pojo.Rol;
import avalon.usuarios.model.request.CreateRolRequest;

import java.util.List;

public interface RolesService {

    List<Rol> getRoles();
    Rol saveRol(CreateRolRequest request);
    Boolean deleteRol(Long rolId);

}
