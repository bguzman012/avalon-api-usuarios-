package avalon.usuarios.data;

import avalon.usuarios.model.pojo.Rol;
import avalon.usuarios.model.pojo.Usuario;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgenteRepository extends BaseUsuarioRepository<Usuario> {

}
