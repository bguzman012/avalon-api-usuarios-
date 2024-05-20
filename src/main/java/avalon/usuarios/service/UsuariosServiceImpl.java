package avalon.usuarios.service;

import avalon.usuarios.data.RolRepository;
import avalon.usuarios.data.UsuarioRepository;
import avalon.usuarios.model.pojo.Rol;
import avalon.usuarios.model.request.CreateUsuarioRequest;
import avalon.usuarios.model.request.UpdateEstadoUsuario;
import avalon.usuarios.model.request.UpdateUsuarioRequest;
import avalon.usuarios.model.pojo.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Service
public class UsuariosServiceImpl implements UsuariosService {

    private final UsuarioRepository repository;
    @Autowired
    private RolRepository rolRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @Autowired
    public UsuariosServiceImpl(UsuarioRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Usuario> getUsuarios() {
        return repository.findAll();
    }

    @Override
    public Usuario getUsuario(Long usuarioId) {
        return repository.findById(usuarioId).orElse(null);
    }

    @Override
    public Usuario updateUsuario(Usuario usuario, UpdateUsuarioRequest request) {
        Rol rol = rolRepository.findById(request.getIdRol()).orElse(null);
        if (rol == null) return null;

        usuario.setContrasenia(request.getContrasenia());
        usuario.setEstado(request.getEstado());
        usuario.setReferenciaPersonal(request.getReferenciaPersonal());
        usuario.setDireccion(request.getDireccion());
        usuario.setNombres(request.getNombres());
        usuario.setApellidos(request.getApellidos());
        usuario.setFechaNacimiento(request.getFechaNacimiento());
        usuario.setTelefono(request.getTelefono());
        usuario.setRol(rol);


        return repository.save(usuario);
    }

    public Usuario createUsuario(CreateUsuarioRequest request) {
        Rol rol = rolRepository.findById(request.getIdRol()).orElse(null);
        if (rol == null) return null;

        Usuario usuario = new Usuario();
        usuario.setUsuario(request.getUsuario());
        usuario.setContrasenia(request.getContrasenia());
        usuario.setCorreoElectronico(request.getCorreoElectronico());
        usuario.setReferenciaPersonal(request.getReferenciaPersonal());
        usuario.setDireccion(request.getDireccion());
        usuario.setEstado(request.getEstado());
        usuario.setNombres(request.getNombres());
        usuario.setApellidos(request.getApellidos());
        usuario.setFechaNacimiento(request.getFechaNacimiento());
        usuario.setTelefono(request.getTelefono());
        usuario.setRol(rol);

        return repository.save(usuario);
    }

    @Override
    public Usuario partiallyUpdateEstadoUsuario(UpdateEstadoUsuario request, Long usuarioId) {
        Usuario usuario = repository.findById(usuarioId).orElse(null);

        if (usuario == null)
            return null;

        usuario.setEstado(request.getEstado());
        repository.save(usuario);
        return usuario;
    }

    @Override
    public Usuario validarCredenciales(String usuario, String contrasenia) {
        Usuario usuarioEncontrado = findByUsuario(usuario);

        if (usuarioEncontrado != null && contrasenia.equals(usuarioEncontrado.getContrasenia()))
            return usuarioEncontrado;

        // Verificar si el usuario existe y si la contraseña coincide
        return null;
    }

    @Override
    public Usuario findByUsuario(String usuario) {
        return repository.findByUsuario(usuario);
    }


}
