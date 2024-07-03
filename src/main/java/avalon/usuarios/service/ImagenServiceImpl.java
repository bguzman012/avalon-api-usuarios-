package avalon.usuarios.service;

import avalon.usuarios.data.AseguradoraRepository;
import avalon.usuarios.data.ImagenRepository;
import avalon.usuarios.data.MembresiaRepository;
import avalon.usuarios.model.pojo.Imagen;
import avalon.usuarios.model.pojo.Membresia;
import avalon.usuarios.model.pojo.Reclamacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ImagenServiceImpl implements ImagenService {

    private final ImagenRepository repository;

    @Autowired
    public ImagenServiceImpl(ImagenRepository repository) {
        this.repository = repository;
    }


    @Override
    public Optional<Imagen> findImagenById(Long imagenId) {
        return repository.findById(imagenId);
    }

    @Override
    public Imagen saveImagen(Imagen imagen) {
        return repository.save(imagen);
    }

    @Override
    public void deleteImagen(Long imagenId) {
        repository.deleteById(imagenId);
    }
}
