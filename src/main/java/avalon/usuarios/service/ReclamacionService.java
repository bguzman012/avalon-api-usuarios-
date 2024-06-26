package avalon.usuarios.service;

import avalon.usuarios.model.pojo.ClientePoliza;
import avalon.usuarios.model.pojo.Reclamacion;
import avalon.usuarios.model.request.PartiallyUpdateReclamacionRequest;

import java.util.List;
import java.util.Optional;

public interface ReclamacionService {

    List<Reclamacion> getReclamacionByEstado(String estado);
    List<Reclamacion> getReclamaciones();
    List<Reclamacion> getReclamacionesByClientePoliza(ClientePoliza clientePoliza);
    Optional<Reclamacion> getReclamacion(Long reclamacionId);
    Optional<Reclamacion> findByIdWithoutImage(Long reclamacionId);
    Reclamacion saveReclamacion(Reclamacion reclamacion);
    Reclamacion partiallyUpdateReclamacion(PartiallyUpdateReclamacionRequest request, Long reclamacionId);
    void deleteReclamacion(Long reclamacionId);
}