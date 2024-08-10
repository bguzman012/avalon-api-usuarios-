package avalon.usuarios.service;

import avalon.usuarios.model.pojo.Caso;
import avalon.usuarios.model.pojo.Emergencia;
import avalon.usuarios.model.pojo.ClientePoliza;
import avalon.usuarios.model.request.PartiallyUpdateEmergenciasRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface EmergenciaService {

    String generarNuevoCodigo();
    Page<Emergencia> searchEmergencias(String busqueda, String estado, Pageable pageable, ClientePoliza clientePoliza, Caso caso);
    Optional<Emergencia> getEmergencia(Long casoId);
    Emergencia saveEmergencia(Emergencia emergencia);
    Emergencia partiallyUpdateEmergencia(PartiallyUpdateEmergenciasRequest request, Long casoId);
    void deleteEmergencia(Long casoId);
}
