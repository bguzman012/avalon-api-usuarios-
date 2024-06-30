package avalon.usuarios.service;

import avalon.usuarios.model.pojo.Estado;
import avalon.usuarios.model.pojo.Pais;
import avalon.usuarios.model.pojo.Rol;
import avalon.usuarios.model.request.CreateRolRequest;

import java.util.List;
import java.util.Optional;

public interface EstadosService {

    List<Estado> findAllByPais(Pais pais);
    Optional<Estado> findById(Long id);

}
