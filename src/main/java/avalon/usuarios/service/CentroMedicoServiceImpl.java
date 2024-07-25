package avalon.usuarios.service;

import avalon.usuarios.data.CentroMedicoRepository;
import avalon.usuarios.model.pojo.CentroMedico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CentroMedicoServiceImpl implements CentroMedicoService {

    private final CentroMedicoRepository repository;

    @Autowired
    public CentroMedicoServiceImpl(CentroMedicoRepository repository) {
        this.repository = repository;
    }


    @Override
    public Page<CentroMedico> searchCentrosMedicos(String busqueda, Pageable pageable) {
        return repository.searchCentrosMedicos(busqueda, pageable);
    }

    @Override
    public Optional<CentroMedico> getCentroMedico(Long centroMedicoId) {
        return repository.findById(centroMedicoId);
    }

    @Override
    public CentroMedico saveCentroMedico(CentroMedico centroMedico) {
        return repository.save(centroMedico);
    }

    @Override
    public void deleteCentroMedico(Long centroMedicoId) {
        repository.deleteById(centroMedicoId);
    }

}
