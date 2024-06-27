package avalon.usuarios.service;

import avalon.usuarios.model.pojo.Asesor;
import avalon.usuarios.model.pojo.Cliente;

import java.util.List;

public interface ClienteService extends UsuariosService<Cliente> {
    List<Cliente> findAllByEstado(String estado);
    List<Cliente> findAll();
}