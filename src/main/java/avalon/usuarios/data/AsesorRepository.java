package avalon.usuarios.data;

import avalon.usuarios.model.pojo.Rol;
import avalon.usuarios.model.pojo.Usuario;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AsesorRepository extends BaseUsuarioRepository<Usuario> {
    Usuario findByNombreUsuario(String nombreUsuario);
    List<Usuario> findAllByRolAndEstado(Rol rol, String estado);
    List<Usuario> findAllByRol(Rol rol);

}
