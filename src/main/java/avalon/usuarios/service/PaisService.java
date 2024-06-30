package avalon.usuarios.service;

import avalon.usuarios.model.pojo.Estado;
import avalon.usuarios.model.pojo.Pais;

import java.util.List;
import java.util.Optional;

public interface PaisService {

    List<Pais> findAll();
    Optional<Pais> findById(Long id);

}
