package avalon.usuarios.data;

import avalon.usuarios.model.pojo.Rol;
import avalon.usuarios.model.pojo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("usuarioRepository")
public interface UsuarioRepository  extends BaseUsuarioRepository<Usuario> {
    Usuario findByNombreUsuario(String nombreUsuario);
    List<Usuario> findAllByRolAndEstado(Rol rol, String estado);
    List<Usuario> findAllByRol(Rol rol);
    List<Usuario> findAllByEstado(String estado);


}
