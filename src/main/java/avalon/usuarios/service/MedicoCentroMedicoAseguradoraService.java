package avalon.usuarios.service;

import avalon.usuarios.model.pojo.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface MedicoCentroMedicoAseguradoraService {

    MedicoCentroMedicoAseguradora saveMedicoCentroMedicoAseguradora(MedicoCentroMedicoAseguradora clienteMembresia);
    Page<MedicoCentroMedicoAseguradora> searchMedicoCentroMedicoAseguradoras(String busqueda, Pageable pageable, Medico medico, Aseguradora aseguradora, CentroMedico centroMedico);
    Optional<MedicoCentroMedicoAseguradora> getMedicoCentroMedicoAseguradora(Long id);
    void deleteMedicoCentroMedicoAseguradora(Long id);
}
