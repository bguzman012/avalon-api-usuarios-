package avalon.usuarios.service;

import avalon.usuarios.model.pojo.Cliente;
import avalon.usuarios.model.pojo.ClienteMembresia;
import avalon.usuarios.model.pojo.ClientePoliza;
import avalon.usuarios.model.pojo.Membresia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ClienteMembresiaService {

    ClienteMembresia saveClienteMembresia(ClienteMembresia clienteMembresia);
    Page<ClienteMembresia> searchClientesMembresias(String busqueda, String estado, Pageable pageable, Cliente cliente, Membresia membresia);
    Optional<ClienteMembresia> getClienteMembresia(Long id);
    void deleteClienteMembresia(Long id);
    ByteArrayOutputStream generateExcelClientesPolizas(String busqueda, String sortField, String sortOrder, Cliente cliente, Membresia membresia) throws IOException;
    List<ClienteMembresia> searchAllClientesMembresias(String busqueda, String sortField, String sortOrder,  Cliente cliente, Membresia membresia);

}
