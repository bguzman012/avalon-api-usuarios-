package avalon.usuarios.service;

import avalon.usuarios.model.request.UpdateEstadoUsuario;
import avalon.usuarios.model.pojo.Usuario;
import avalon.usuarios.model.request.CreateUsuarioRequest;
import avalon.usuarios.model.request.UpdateUsuarioRequest;

import java.util.List;

public interface UsuariosService {

    List<Usuario> getUsuarios();
    Usuario getUsuario(Long usuarioId);
    Usuario createUsuario(CreateUsuarioRequest request);
    Usuario updateUsuario(Usuario usuario, UpdateUsuarioRequest request);
    Usuario partiallyUpdateEstadoUsuario(UpdateEstadoUsuario request, Long usuarioId);
    Usuario validarCredenciales(String nombreUsuario, String contrasenia);
    Usuario findByNombreUsuario(String nombreUsuario);
    List<Usuario> getUsuariosByRol(Long rolId);
}
