package avalon.usuarios.service;

import avalon.usuarios.model.pojo.ClientePoliza;
import avalon.usuarios.model.request.ClientePolizaRequest;

import java.util.List;
import java.util.Optional;

public interface ClientesPolizaService {

    List<ClientePoliza> getClientesPolizas();
    List<ClientePoliza> getClientesPolizasByPoliza(Long polizaId);
    Optional<ClientePoliza> getClientePoliza(Long clientePolizaId);
    ClientePoliza savePoliza(ClientePoliza request);
    void deleteClientePoliza(Long clientePolizaId);
}
