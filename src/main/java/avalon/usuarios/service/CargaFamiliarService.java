package avalon.usuarios.service;

import avalon.usuarios.model.pojo.Asesor;
import avalon.usuarios.model.pojo.CargaFamiliar;
import avalon.usuarios.model.pojo.Cliente;
import avalon.usuarios.model.pojo.ClientePoliza;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface CargaFamiliarService {
    Page<ClientePoliza> searchCargasByClientePoliza(String busqueda, ClientePoliza clientePoliza, Pageable pageable);
    ByteArrayOutputStream generateExcelClientesPolizas(ClientePoliza clientePoliza, String busqueda, String sortField, String sortOrder) throws IOException;
    List<ClientePoliza> searchAllCargasFamiliares(ClientePoliza clientePoliza, String busqueda, String sortField, String sortOrder);

}