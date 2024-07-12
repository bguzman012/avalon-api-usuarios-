package avalon.usuarios.service;

import avalon.usuarios.data.CitaMedicaRepository;
import avalon.usuarios.model.pojo.ClientePoliza;
import avalon.usuarios.model.pojo.CitaMedica;
import avalon.usuarios.model.request.PartiallyUpdateCitaMedicaRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CitaMedicaServiceImpl implements CitaMedicaService {

    private final CitaMedicaRepository repository;

    @Autowired
    public CitaMedicaServiceImpl(CitaMedicaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<CitaMedica> getCitasMedicasByClientePoliza(ClientePoliza clientePoliza) {
        return repository.findByClientePoliza(clientePoliza);
    }

    @Override
    public List<CitaMedica> getCitaMedicaByEstado(String estado) {
        return repository.findAllByEstado(estado);
    }

    @Override
    public List<CitaMedica> getCitasMedicas() {
        return repository.findAll();
    }

    @Override
    public Optional<CitaMedica> getCitaMedica(Long citaMedicaId) {
        return repository.findById(citaMedicaId);
    }

    @Override
    public CitaMedica saveCitaMedica(CitaMedica citaMedica) {
        return repository.save(citaMedica);
    }

    @Override
    public CitaMedica partiallyUpdateCitaMedica(PartiallyUpdateCitaMedicaRequest request, Long citaMedicaId) {
        CitaMedica citaMedica = repository.findById(citaMedicaId).orElse(null);
        if (citaMedica == null) return null;

        if (request.getEstado() != null)
            citaMedica.setEstado(request.getEstado());

        return repository.save(citaMedica);
    }

    @Override
    public void deleteCitaMedica(Long citaMedicaId) {
        repository.deleteById(citaMedicaId);
    }
}
