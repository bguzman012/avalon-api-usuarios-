package avalon.usuarios.service;

import avalon.usuarios.data.EstadoRepository;
import avalon.usuarios.data.RolRepository;
import avalon.usuarios.model.pojo.Estado;
import avalon.usuarios.model.pojo.Pais;
import avalon.usuarios.model.pojo.Rol;
import avalon.usuarios.model.request.CreateRolRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EstadosServiceImpl implements EstadosService {

    private final EstadoRepository repository;

    @Autowired
    public EstadosServiceImpl(EstadoRepository repository) {
        this.repository = repository;
    }


    @Override
    public List<Estado> findAllByPais(Pais pais) {
        return this.repository.findAllByPais(pais);
    }

    @Override
    public Optional<Estado> findById(Long id) {
        return this.repository.findById(id);
    }

    @Override
    public Optional<Estado> findByNombre(String nombre) {
        return repository.findByNombreContainingIgnoreCase(nombre);
    }
}
