package avalon.usuarios.service;

import avalon.usuarios.model.pojo.Cliente;
import avalon.usuarios.model.pojo.ClienteMembresia;
import avalon.usuarios.model.pojo.Membresia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ClienteMembresiaService {

    ClienteMembresia saveClienteMembresia(ClienteMembresia clienteMembresia);
    Page<ClienteMembresia> searchClientesMembresias(String busqueda, Pageable pageable, Cliente cliente, Membresia membresia);
    Optional<ClienteMembresia> getClienteMembresia(Long id);
    void deleteClienteMembresia(Long id);
}
