package avalon.usuarios.data;

import avalon.usuarios.model.pojo.Asesor;
import avalon.usuarios.model.pojo.Cliente;
import avalon.usuarios.model.pojo.Rol;
import avalon.usuarios.model.pojo.Usuario;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("clienteRepository")
public interface ClienteRepository extends BaseUsuarioRepository<Cliente> {



}
