package avalon.usuarios.service;

import avalon.usuarios.model.pojo.Imagen;
import avalon.usuarios.model.pojo.Membresia;

import java.util.List;
import java.util.Optional;

public interface ImagenService {

    Optional<Imagen> findImagenById(Long imagenId);
    Imagen saveImagen(Imagen imagen);
    void deleteImagen(Long imagenId);
}
