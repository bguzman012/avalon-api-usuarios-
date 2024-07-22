package avalon.usuarios.service;

import avalon.usuarios.model.pojo.Caso;
import avalon.usuarios.model.pojo.ClientePoliza;
import avalon.usuarios.model.request.PartiallyUpdateCasoRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CasoService {

    String generarNuevoCodigo();
    Page<Caso> searchCasos(String busqueda, String estado, Pageable pageable, ClientePoliza clientePoliza);
    Optional<Caso> getCaso(Long casoId);
    Caso saveCaso(Caso caso);
    Caso partiallyUpdateCaso(PartiallyUpdateCasoRequest request, Long casoId);
    void deleteCaso(Long casoId);
}
