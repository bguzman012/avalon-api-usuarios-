package avalon.usuarios.service;

import avalon.usuarios.model.pojo.*;
import avalon.usuarios.model.request.PartiallyUpdateCitaMedicaRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface CitaMedicaService {

    String generarNuevoCodigo();

    Page<CitaMedica> searchCitasMedicas(String busqueda, String estado, Pageable pageable, ClientePoliza clientePoliza, Caso caso, Usuario usuario);

    Optional<CitaMedica> getCitaMedica(Long citaMedicaId);

    CitaMedica saveCitaMedica(CitaMedica citaMedica);

    CitaMedica partiallyUpdateCitaMedica(PartiallyUpdateCitaMedicaRequest request, Long citaMedicaId);

    void deleteCitaMedica(Long citaMedicaId);

    List<CitaMedica> searchAllCitasMedicas(String busqueda, String sortField, String sortOrder, Caso caso);

    ByteArrayOutputStream generateExcelCitasMedicas(String busqueda, String sortField, String sortOrder, Caso caso) throws IOException;

}
