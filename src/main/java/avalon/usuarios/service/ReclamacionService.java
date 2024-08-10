package avalon.usuarios.service;

import avalon.usuarios.model.pojo.Caso;
import avalon.usuarios.model.pojo.ClientePoliza;
import avalon.usuarios.model.pojo.Reclamacion;
import avalon.usuarios.model.request.PartiallyUpdateReclamacionRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ReclamacionService {

    String generarNuevoCodigo();
    Page<Reclamacion> searchReclamaciones(String busqueda, String estado, Pageable pageable, ClientePoliza clientePoliza, Caso caso);
    Optional<Reclamacion> getReclamacion(Long reclamacionId);
    Reclamacion saveReclamacion(Reclamacion reclamacion);
    Reclamacion partiallyUpdateReclamacion(PartiallyUpdateReclamacionRequest request, Long reclamacionId);
    void deleteReclamacion(Long reclamacionId);
}