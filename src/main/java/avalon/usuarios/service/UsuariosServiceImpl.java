package avalon.usuarios.service;

import avalon.usuarios.data.BaseUsuarioRepository;
import avalon.usuarios.data.RolRepository;
import avalon.usuarios.data.UsuarioRepository;
import avalon.usuarios.model.pojo.Rol;
import avalon.usuarios.model.pojo.Usuario;
import avalon.usuarios.model.request.PartiallyUpdateUsuario;
import avalon.usuarios.service.mail.MailService;
import avalon.usuarios.util.PasswordGenerator;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.swing.text.html.parser.Entity;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class UsuariosServiceImpl<T extends Usuario> implements UsuariosService<T> {

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
    private final String ROL_ASESOR = "ASR";
    private final String ROL_AGENTE = "BRO";

    public String generarNombreUsuario(String correoElectronico) {
        String baseNombreUsuario = correoElectronico.split("@")[0];
        String nombreUsuario = baseNombreUsuario;
        int counter = 1;

        // Verifica si el nombre de usuario ya existe
        while (usuarioRepository.existsByNombreUsuario(nombreUsuario)) {
            nombreUsuario = baseNombreUsuario + counter;
            counter++;
        }

        return nombreUsuario;
    }

    @Override
    public T save(T entity) throws MessagingException, IOException {
        //Se agrega el nombre de usuario cuando se crea
        if (entity.getId() == null)
            entity.setNombreUsuario(generarNombreUsuario(entity.getCorreoElectronico()));

        if (entity.getId() == null && (entity.getRol().getCodigo().equals(this.ROL_AGENTE) || entity.getRol().getCodigo().equals(this.ROL_ASESOR))) {
            String contraseniaTemporal = PasswordGenerator.generateTemporaryPassword();

            entity.setContrasenia(passwordEncoder.encode(contraseniaTemporal));

            // Guardamos la contraseña temporal en texto
            entity.setContraseniaTemporal(contraseniaTemporal);
            entity.setEstado(this.ESTADO_ACTIVO);

            T entitySave = this.repository.save(entity);

            if (entitySave.getId() != null) {
                String textoMail = "<p><b>" + entity.getNombres() + " " + entity.getNombresDos() + " "
                        + entity.getApellidos() + " " + entity.getApellidosDos() + " [" + entity.getNombreUsuario() +
                        "]</b></p>" +
                        "<p>Su usuario ha sido creado con éxito por parte del Administrador de Avalon. La contraseña temporal para su primer " +
                        "inicio de sesión es la siguiente: </p>" +
                        "<p><b>" + contraseniaTemporal + "</b></p>";

                this.mailService.sendHtmlEmail(entity.getCorreoElectronico(), "Avalon Usuario Creado", textoMail);
            }

            return entitySave;
        }

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
    public T partiallyUpdateUsuario(PartiallyUpdateUsuario request, T entity) throws MessagingException, IOException {
        if (request.getEstado() != null && request.getEstado().equals(this.ESTADO_ACTIVO)
                && entity.getEstado().equals(this.ESTADO_PENDIENTE_APROBACION) && entity.getRol().getCodigo().equals(this.ROL_CLIENTE)) {
            String contraseniaTemporal = PasswordGenerator.generateTemporaryPassword();

            entity.setContrasenia(passwordEncoder.encode(contraseniaTemporal));

            // Guardamos la contraseña temporal en texto
            entity.setContraseniaTemporal(contraseniaTemporal);
            entity.setEstado(this.ESTADO_ACTIVO);

            // Descomentar para enviar correo de
            String textoMail = "<p><b>" + entity.getNombres() + " " + entity.getNombresDos() + " "
                    + entity.getApellidos() + " " + entity.getApellidosDos() + " [" + entity.getNombreUsuario() +
                    "]</b></p>" +
                    "<p>Su usuario ha sido aprobado con éxito por parte del Administrador de Avalon. La contraseña temporal para su primer " +
                    "inicio de sesión es la siguiente: </p>" +
                    "<p><b>" + contraseniaTemporal + "</b></p>";

            this.mailService.sendHtmlEmail(entity.getCorreoElectronico(), "Avalon Usuario aprobado", textoMail);
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

    @Override
    public void actualizarContrasenia(Usuario usuario, String nuevaContrasenia) {
        if (usuario.getContraseniaTemporalModificada().equals(Boolean.FALSE)) {
            usuario.setContraseniaTemporal(null);
            usuario.setContraseniaTemporalModificada(Boolean.TRUE);
        }

        usuario.setContrasenia(passwordEncoder.encode(nuevaContrasenia));
        this.usuarioRepository.save(usuario);
    }

    @Override
    public String generateCodigo2FA() {
        return String.valueOf((int) ((Math.random() * 900000) + 100000));
    }

    //
    @Override
    public Usuario findByNombreUsuario(String nombreUsuario) {
        return this.usuarioRepository.findByNombreUsuario(nombreUsuario);
    }

    @Override
    public Usuario getUsuario(Long usuarioId) {
        return repository.findById(usuarioId).orElse(null);
    }

    @Override
    public Boolean existeByNombreUsuario(String nombreUsuario) {
        return this.usuarioRepository.existsByNombreUsuario(nombreUsuario);
    }


}
