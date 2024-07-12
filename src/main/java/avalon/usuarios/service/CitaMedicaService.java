package avalon.usuarios.service;

import avalon.usuarios.model.pojo.ClientePoliza;
import avalon.usuarios.model.pojo.CitaMedica;
import avalon.usuarios.model.request.PartiallyUpdateCitaMedicaRequest;

import java.util.List;
import java.util.Optional;

public interface CitaMedicaService {

    List<CitaMedica> getCitaMedicaByEstado(String estado);
    List<CitaMedica> getCitasMedicas();
    List<CitaMedica> getCitasMedicasByClientePoliza(ClientePoliza clientePoliza);
    Optional<CitaMedica> getCitaMedica(Long citaMedicaId);
    CitaMedica saveCitaMedica(CitaMedica citaMedica);
    CitaMedica partiallyUpdateCitaMedica(PartiallyUpdateCitaMedicaRequest request, Long citaMedicaId);
    void deleteCitaMedica(Long citaMedicaId);
}
