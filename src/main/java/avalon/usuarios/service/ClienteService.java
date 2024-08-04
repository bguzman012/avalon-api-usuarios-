package avalon.usuarios.service;

import avalon.usuarios.model.pojo.Asesor;
import avalon.usuarios.model.pojo.Cliente;
import avalon.usuarios.model.pojo.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ClienteService extends UsuariosService<Cliente> {
    Page<Cliente> findAll(Pageable pageable);
    Page<Cliente> findAllByEstado(String estado, Pageable pageable);
    Page<Cliente> searchClientes(String estado, String busqueda, Pageable pageable, Usuario usuario);
}