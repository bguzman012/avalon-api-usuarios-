package avalon.usuarios.service;

import avalon.usuarios.model.pojo.*;
import avalon.usuarios.model.request.ClientePolizaRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ClientesPolizaService {

    String generarNuevoCodigo();
    Page<ClientePoliza> searchClienesPolizas(String busqueda, Pageable pageable, Cliente cliente, Poliza poliza);
    Optional<ClientePoliza> getClientePoliza(Long clientePolizaId);
    ClientePoliza savePoliza(ClientePoliza request);
    void deleteClientePoliza(Long clientePolizaId);
}
