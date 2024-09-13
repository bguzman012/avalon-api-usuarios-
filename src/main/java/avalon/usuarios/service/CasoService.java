package avalon.usuarios.service;

import avalon.usuarios.model.pojo.Caso;
import avalon.usuarios.model.pojo.CitaMedica;
import avalon.usuarios.model.pojo.ClientePoliza;
import avalon.usuarios.model.pojo.Usuario;
import avalon.usuarios.model.response.PaginatedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface CasoService {

    Page<Caso> searchCasos(String busqueda, Pageable pageable, ClientePoliza clientePoliza, Usuario usuario);
    Optional<Caso> getCaso(Long casoId);
    Caso saveCaso(Caso caso);
    void deleteCaso(Long casoId);
    List<Caso> searchAllCasos(String busqueda, String sortField, String sortOrder, ClientePoliza clientePoliza);
    PaginatedResponse<Object> getCasosTrack(String busqueda, int pageNumber, int pageSize);
    ByteArrayOutputStream generateExcelCasosTrack(String busqueda) throws IOException;
    ByteArrayOutputStream generateExcelCasos(String busqueda, String sortField, String sortOrder, ClientePoliza clientePoliza) throws IOException;

}
