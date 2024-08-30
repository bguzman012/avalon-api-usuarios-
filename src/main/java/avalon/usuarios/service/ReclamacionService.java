package avalon.usuarios.service;

import avalon.usuarios.model.pojo.*;
import avalon.usuarios.model.request.PartiallyUpdateReclamacionRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ReclamacionService {

    String generarNuevoCodigo();
    Page<Reclamacion> searchReclamaciones(String busqueda, String estado, Pageable pageable, ClientePoliza clientePoliza, Caso caso, Usuario usuario);
    Optional<Reclamacion> getReclamacion(Long reclamacionId);
    Reclamacion saveReclamacion(Reclamacion reclamacion);
    Reclamacion partiallyUpdateReclamacion(PartiallyUpdateReclamacionRequest request, Long reclamacionId);
    void deleteReclamacion(Long reclamacionId);
    List<Reclamacion> searchAllReclamaciones(String busqueda, String sortField, String sortOrder, Caso caso);
    ByteArrayOutputStream generateExcelReclamaciones(String busqueda, String sortField, String sortOrder, Caso caso) throws IOException;
}