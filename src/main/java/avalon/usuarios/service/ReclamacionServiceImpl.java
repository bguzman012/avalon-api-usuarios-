package avalon.usuarios.service;

import avalon.usuarios.data.AseguradoraRepository;
import avalon.usuarios.data.ReclamacionRepository;
import avalon.usuarios.data.UsuarioRepository;
import avalon.usuarios.model.pojo.Aseguradora;
import avalon.usuarios.model.pojo.ClientePoliza;
import avalon.usuarios.model.pojo.Reclamacion;
import avalon.usuarios.model.request.PartiallyUpdateAseguradora;
import avalon.usuarios.model.request.PartiallyUpdateReclamacionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReclamacionServiceImpl implements ReclamacionService {

    private final ReclamacionRepository repository;

    @Autowired
    public ReclamacionServiceImpl(ReclamacionRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Reclamacion> getReclamacionesByClientePoliza(ClientePoliza clientePoliza) {
        return repository.findByClientePoliza(clientePoliza);
    }


    @Override
    public List<Reclamacion> getReclamacionByEstado(String estado) {
        return repository.findAllByEstado(estado);
    }

    @Override
    public List<Reclamacion> getReclamaciones() {
        return repository.findAll();
    }

    @Override
    public Optional<Reclamacion> getReclamacion(Long reclamacionId) {
        return repository.findById(reclamacionId);
    }

    @Override
    public Reclamacion saveReclamacion(Reclamacion reclamacion) {
        return repository.save(reclamacion);
    }

    @Override
    public Reclamacion partiallyUpdateReclamacion(PartiallyUpdateReclamacionRequest request, Long reclamacionId) {
        Reclamacion reclamacion = repository.findById(reclamacionId).orElse(null);
        if (reclamacion == null) return null;

        if (request.getEstado() != null)
            reclamacion.setEstado(request.getEstado());

        return repository.save(reclamacion);
    }

    @Override
    public void deleteReclamacion(Long reclamacionId) {
        repository.deleteById(reclamacionId);
    }
}