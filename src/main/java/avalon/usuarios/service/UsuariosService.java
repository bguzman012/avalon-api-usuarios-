package avalon.usuarios.service;

import avalon.usuarios.model.pojo.Usuario;
import avalon.usuarios.model.request.PartiallyUpdateUsuario;
import jakarta.mail.MessagingException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface UsuariosService<T extends Usuario> {

    T save(T entity);
    Optional<T> findById(Long id);
    List<T> findAll();
    List<T> findAllByEstado(String estado);
    void deleteById(Long id);
    //    List<Usuario> getUsuarios();
//    Usuario getUsuario(Long usuarioId);
//    Usuario createUsuario(CreateUsuarioRequest request);
//    Usuario updateUsuario(Usuario usuario, UpdateUsuarioRequest request);
    T partiallyUpdateUsuario(PartiallyUpdateUsuario request, T entity) throws MessagingException, IOException;
    Usuario validarCredenciales(String nombreUsuario, String contrasenia);
    void actualizarContrasenia(Usuario usuario, String nuevaContrasenia);
    Usuario findByNombreUsuario(String nombreUsuario);
    Usuario getUsuario(Long usuarioId);
//    List<Usuario> getUsuariosByRolAndEstado(Long rolId, String estado);
}
