package avalon.usuarios.service;

import avalon.usuarios.model.request.PartiallyUpdateUsuario;
import avalon.usuarios.model.pojo.Usuario;
import avalon.usuarios.model.request.CreateUsuarioRequest;
import avalon.usuarios.model.request.UpdateUsuarioRequest;

import java.util.List;

public interface UsuariosService {

    List<Usuario> getUsuarios();
    Usuario getUsuario(Long usuarioId);
    Usuario createUsuario(CreateUsuarioRequest request);
    Usuario updateUsuario(Usuario usuario, UpdateUsuarioRequest request);
    Usuario partiallyUpdateUsuario(PartiallyUpdateUsuario request, Long usuarioId);
    Usuario validarCredenciales(String nombreUsuario, String contrasenia);
    Usuario findByNombreUsuario(String nombreUsuario);
    List<Usuario> getUsuariosByRolAndEstado(Long rolId, String estado);
}
