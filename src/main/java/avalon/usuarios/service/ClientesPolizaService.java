package avalon.usuarios.service;

import avalon.usuarios.model.pojo.ClientePoliza;
import avalon.usuarios.model.pojo.Poliza;
import avalon.usuarios.model.request.CreateClientePolizaRequest;
import avalon.usuarios.model.request.CreatePolizaRequest;
import avalon.usuarios.model.request.UpdateClientePolizaRequest;
import avalon.usuarios.model.request.UpdatePolizaRequest;

import java.util.List;

public interface ClientesPolizaService {

    List<ClientePoliza> getClientesPolizas();
    List<ClientePoliza> getClientesPolizasByPoliza(Long polizaId);
    ClientePoliza getClientePoliza(Long clientePolizaId);
    ClientePoliza createClientePoliza(CreateClientePolizaRequest request);
    ClientePoliza updateClientePoliza(ClientePoliza clientePoliza, UpdateClientePolizaRequest request);
    void deleteClientePoliza(Long clientePolizaId);
}
