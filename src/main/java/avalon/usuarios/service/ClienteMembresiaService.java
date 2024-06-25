package avalon.usuarios.service;

import avalon.usuarios.model.pojo.ClienteMembresia;

import java.util.List;
import java.util.Optional;

public interface ClienteMembresiaService {

    ClienteMembresia saveClienteMembresia(ClienteMembresia clienteMembresia);
    List<ClienteMembresia> getClientesMembresias();
    List<ClienteMembresia> getClientesMembresiasByMembresia(Long membresiaId);
    List<ClienteMembresia> getClientesMembresiasByCliente(Long clienteId);
    Optional<ClienteMembresia> getClienteMembresia(Long id);
    void deleteClienteMembresia(Long id);
}
