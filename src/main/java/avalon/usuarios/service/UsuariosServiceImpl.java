package avalon.usuarios.service;

import avalon.usuarios.data.BaseUsuarioRepository;
import avalon.usuarios.data.RolRepository;
import avalon.usuarios.data.UsuarioRepository;
import avalon.usuarios.model.pojo.Rol;
import avalon.usuarios.model.pojo.Usuario;
import avalon.usuarios.model.request.PartiallyUpdateUsuario;
import avalon.usuarios.service.mail.MailService;
import avalon.usuarios.util.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@Service
public class UsuariosServiceImpl <T extends Usuario> implements UsuariosService<T> {

    @Autowired
    @Qualifier("baseUsuarioRepository")
    protected BaseUsuarioRepository<T> repository;
    @Autowired
    @Qualifier("usuarioRepository")
    private UsuarioRepository usuarioRepository;
    @Autowired
    private RolRepository rolRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MailService mailService;

    private final String ESTADO_PENDIENTE_APROBACION = "P";
    private final String ESTADO_ACTIVO = "A";
    private final String ROL_CLIENTE = "CLI";

    @Override
    public T save(T entity) {
        return repository.save(entity);
    }

    @Override
    public Optional<T> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<T> findAll() {
        return repository.findAll();
    }

    @Override
    public List<T> findAllByEstado(String estado) {
        return this.repository.findAllByEstado(estado);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public T partiallyUpdateUsuario(PartiallyUpdateUsuario request, T entity) {
        if (request.getEstado() != null && request.getEstado().equals(this.ESTADO_ACTIVO)
                && entity.getEstado().equals(this.ESTADO_PENDIENTE_APROBACION) && entity.getRol().getCodigo().equals(this.ROL_CLIENTE)) {
            String contraseniaTemporal = PasswordGenerator.generateTemporaryPassword();

            entity.setContrasenia(passwordEncoder.encode(contraseniaTemporal));

            // Guardamos la contraseña temporal en texto
            entity.setContraseniaTemporal(contraseniaTemporal);
            entity.setEstado(this.ESTADO_ACTIVO);

            // Descomentar para enviar correo de
            String textoMail = "Estimado cliente " + entity.getNombres() + " " + entity.getNombresDos() + " "
                    + entity.getApellidos() + " " + entity.getApellidosDos() + " [" + entity.getNombreUsuario() +
                    "], su usuario ha sido aprobado con éxito por parte del Administrador de Avalon. La contraseña temporal para su primer " +
                    "inicio de sesión es la siguiente: " + contraseniaTemporal;

            this.mailService.sendSimpleEmail(entity.getCorreoElectronico(), "Avalon Usuario aprobado", textoMail);
        }

        if (request.getContrasenia() != null)
            entity.setContrasenia(passwordEncoder.encode(request.getContrasenia()));

        return this.save(entity);
    }

    @Override
    public Usuario validarCredenciales(String nombreUsuario, String contrasenia) {
        Usuario usuarioEncontrado = findByNombreUsuario(nombreUsuario);
        if (usuarioEncontrado != null && passwordEncoder.matches(contrasenia, usuarioEncontrado.getContrasenia())) {
            return usuarioEncontrado;
        }
        return null;
    }
    //
    @Override
    public Usuario findByNombreUsuario(String nombreUsuario) {
        return this.usuarioRepository.findByNombreUsuario(nombreUsuario);
    }

//    @Override
//    public Usuario findByNombreUsuario(String nombreUsuario) {
//        return null;
//    }

    //    private final UsuarioRepository repository;
//    private final RolRepository rolRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    @Autowired
//    private UsuarioAseguradoraRepository usuarioAseguradoraRepository;
//
//    @Autowired
//    public UsuariosServiceImpl(UsuarioRepository repository, RolRepository rolRepository, PasswordEncoder passwordEncoder) {
//        this.repository = repository;
//        this.rolRepository = rolRepository;
//        this.passwordEncoder = passwordEncoder;
//    }
//
//    @Override
//    public List<Usuario> getUsuarios() {
//        return repository.findAll();
//    }
//
    @Override
    public Usuario getUsuario(Long usuarioId) {
        return repository.findById(usuarioId).orElse(null);
    }

//    @Override
//    public Usuario createUsuario(CreateUsuarioRequest request) {
//        Rol rol = rolRepository.findById(request.getRolId()).orElse(null);
//        if (rol == null) return null;
//
//        Usuario usuario = new Usuario();
//        usuario.setNombreUsuario(request.getNombreUsuario());
//        usuario.setContrasenia(passwordEncoder.encode(request.getContrasenia()));
//        usuario.setCorreoElectronico(request.getCorreoElectronico());
//        usuario.setEstado(request.getEstado());
//        usuario.setNombres(request.getNombres());
//        usuario.setApellidos(request.getApellidos());
//        usuario.setFechaNacimiento(request.getFechaNacimiento());
//        usuario.setNumeroTelefono(request.getNumeroTelefono());
//        usuario.setRol(rol);
//
//        return repository.save(usuario);
//    }
//
//    @Override
//    public Usuario updateUsuario(Usuario usuario, UpdateUsuarioRequest request) {
//        Rol rol = rolRepository.findById(request.getRolId()).orElse(null);
//        if (rol == null) return null;
//
//        usuario.setEstado(request.getEstado());
//        usuario.setNombres(request.getNombres());
//        usuario.setApellidos(request.getApellidos());
//        usuario.setFechaNacimiento(request.getFechaNacimiento());
//        usuario.setNumeroTelefono(request.getNumeroTelefono());
//        usuario.setRol(rol);
//
//        return repository.save(usuario);
//    }
//
//    @Override
//    public Usuario partiallyUpdateUsuario(PartiallyUpdateUsuario request, Long usuarioId) {
//        Usuario usuario = repository.findById(usuarioId).orElse(null);
//        if (usuario == null) return null;
//
//        if (request.getEstado() != null)
//            usuario.setEstado(request.getEstado());
//
//        if (request.getEstado() != null && request.getEstado().equals("I")){
//            usuarioAseguradoraRepository.deleteAllByUsuarioAseguradorByUsuario(usuario);
//        }
//
//        if (request.getContrasenia() != null)
//            usuario.setContrasenia(passwordEncoder.encode(request.getContrasenia()));
//
//        return repository.save(usuario);
//    }
//
//    @Override
//    public Usuario validarCredenciales(String nombreUsuario, String contrasenia) {
//        Usuario usuarioEncontrado = findByNombreUsuario(nombreUsuario);
////        String contraseniaEncriptada = passwordEncoder.encode("admin");
//        if (usuarioEncontrado != null && passwordEncoder.matches(contrasenia, usuarioEncontrado.getContrasenia())) {
//            return usuarioEncontrado;
//        }
//        return null;
//    }
//
//    @Override
//    public Usuario findByNombreUsuario(String nombreUsuario) {
//        return repository.findByNombreUsuario(nombreUsuario);
//    }
//
//    @Override
//    public List<Usuario> getUsuariosByRolAndEstado(Long rolId, String estado) {
//        Rol rol = rolRepository.findById(rolId).orElse(null);
//
//        if (rol == null) return Collections.emptyList();
//
//        if (estado.equals("T"))
//            return repository.findAllByRol(rol);
//
//        return repository.findAllByRolAndEstado(rol, estado);
//    }
//

}
