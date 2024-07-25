package avalon.usuarios.service;

import avalon.usuarios.model.pojo.CentroMedico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CentroMedicoService {

    Page<CentroMedico> searchCentrosMedicos(String busqueda, Pageable pageable);
    Optional<CentroMedico> getCentroMedico(Long centroMedicoId);
    CentroMedico saveCentroMedico(CentroMedico centroMedico);
    void deleteCentroMedico(Long centroMedicoId);
}
