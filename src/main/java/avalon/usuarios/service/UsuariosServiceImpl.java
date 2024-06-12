package avalon.usuarios.service;

import avalon.usuarios.data.RolRepository;
import avalon.usuarios.data.UsuarioAseguradoraRepository;
import avalon.usuarios.data.UsuarioRepository;
import avalon.usuarios.model.pojo.Rol;
import avalon.usuarios.model.request.CreateUsuarioRequest;
import avalon.usuarios.model.request.PartiallyUpdateUsuario;
import avalon.usuarios.model.request.UpdateUsuarioRequest;
import avalon.usuarios.model.pojo.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;

@Service
public class UsuariosServiceImpl implements UsuariosService {

    private final UsuarioRepository repository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioAseguradoraRepository usuarioAseguradoraRepository;

    @Autowired
    public UsuariosServiceImpl(UsuarioRepository repository, RolRepository rolRepository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
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
    public Usuario createUsuario(CreateUsuarioRequest request) {
        Rol rol = rolRepository.findById(request.getRolId()).orElse(null);
        if (rol == null) return null;

        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(request.getNombreUsuario());
        usuario.setContrasenia(passwordEncoder.encode(request.getContrasenia()));
        usuario.setCorreoElectronico(request.getCorreoElectronico());
        usuario.setEstado(request.getEstado());
        usuario.setNombres(request.getNombres());
        usuario.setApellidos(request.getApellidos());
        usuario.setFechaNacimiento(request.getFechaNacimiento());
        usuario.setNumeroTelefono(request.getNumeroTelefono());
        usuario.setRol(rol);

        return repository.save(usuario);
    }

    @Override
    public Usuario updateUsuario(Usuario usuario, UpdateUsuarioRequest request) {
        Rol rol = rolRepository.findById(request.getRolId()).orElse(null);
        if (rol == null) return null;

        usuario.setEstado(request.getEstado());
        usuario.setNombres(request.getNombres());
        usuario.setApellidos(request.getApellidos());
        usuario.setFechaNacimiento(request.getFechaNacimiento());
        usuario.setNumeroTelefono(request.getNumeroTelefono());
        usuario.setRol(rol);

        return repository.save(usuario);
    }

    @Override
    public Usuario partiallyUpdateUsuario(PartiallyUpdateUsuario request, Long usuarioId) {
        Usuario usuario = repository.findById(usuarioId).orElse(null);
        if (usuario == null) return null;

        if (request.getEstado() != null)
            usuario.setEstado(request.getEstado());

        if (request.getEstado() != null && request.getEstado().equals("I")){
            usuarioAseguradoraRepository.deleteAllByUsuarioAseguradorByUsuario(usuario);
        }

        if (request.getContrasenia() != null)
            usuario.setContrasenia(passwordEncoder.encode(request.getContrasenia()));

        return repository.save(usuario);
    }

    @Override
    public Usuario validarCredenciales(String nombreUsuario, String contrasenia) {
        Usuario usuarioEncontrado = findByNombreUsuario(nombreUsuario);
//        String contraseniaEncriptada = passwordEncoder.encode("admin");
        if (usuarioEncontrado != null && passwordEncoder.matches(contrasenia, usuarioEncontrado.getContrasenia())) {
            return usuarioEncontrado;
        }
        return null;
    }

    @Override
    public Usuario findByNombreUsuario(String nombreUsuario) {
        return repository.findByNombreUsuario(nombreUsuario);
    }

    @Override
    public List<Usuario> getUsuariosByRolAndEstado(Long rolId, String estado) {
        Rol rol = rolRepository.findById(rolId).orElse(null);

        if (rol == null) return Collections.emptyList();

        return repository.findAllByRolAndEstado(rol, estado);
    }


}
