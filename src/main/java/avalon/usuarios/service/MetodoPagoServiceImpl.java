package avalon.usuarios.service;

import avalon.usuarios.data.MetodoPagoRepository;
import avalon.usuarios.data.UsuarioRepository;
import avalon.usuarios.model.pojo.MetodoPago;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MetodoPagoServiceImpl implements MetodoPagoService {

    private final MetodoPagoRepository repository;

    @Autowired
    public MetodoPagoServiceImpl(MetodoPagoRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<MetodoPago> searchMetodosPago() {
        return repository.findAll();
    }

    @Override
    public Optional<MetodoPago> getMetodoPago(Long metodoPagoId) {
        return repository.findById(metodoPagoId);
    }

    @Override
    public MetodoPago saveMetodoPago(MetodoPago metodoPago) {
        return repository.save(metodoPago);
    }

    @Override
    public void deleteMetodoPago(Long metodoPagoId) {
        repository.deleteById(metodoPagoId);
    }

}
