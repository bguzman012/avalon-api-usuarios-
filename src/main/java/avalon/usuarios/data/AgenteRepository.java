package avalon.usuarios.data;

import avalon.usuarios.model.pojo.Agente;
import avalon.usuarios.model.pojo.Rol;
import avalon.usuarios.model.pojo.Usuario;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("agenteRepository")
public interface AgenteRepository extends BaseUsuarioRepository<Agente> {

}
