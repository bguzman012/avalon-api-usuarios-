package avalon.usuarios.controller;

import avalon.usuarios.model.pojo.Cliente;
import avalon.usuarios.model.pojo.VerificationCode;
import avalon.usuarios.model.request.*;
import avalon.usuarios.model.response.JwtAuthenticationResponse;
import avalon.usuarios.config.JwtTokenProvider;
import avalon.usuarios.model.pojo.Usuario;
import avalon.usuarios.model.response.ApiResponse;
import avalon.usuarios.service.UsuariosService;
import avalon.usuarios.service.VerificationCodeService;
import avalon.usuarios.service.mail.MailService;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

@RestController
@Slf4j
public class AuthController {

    private final UsuariosService service;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private VerificationCodeService verificationCodeService;
    @Autowired
    private MailService mailService;
    private final int ROL_CLIENTE = 3;

    @Autowired
    public AuthController(@Qualifier("usuariosServiceImpl") UsuariosService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest,
                                              @RequestParam(required = false) String generate2FA) throws MessagingException, IOException {
        String usuario = loginRequest.getUsuario();
        String contrasenia = loginRequest.getContrasenia();

        Usuario usuarioEncontrado = service.validarCredenciales(usuario, contrasenia);

        // Validar credenciales
        if (usuarioEncontrado != null) {
            if (usuarioEncontrado.getContraseniaTemporalModificada().equals(Boolean.FALSE) &&
                    Objects.equals(usuarioEncontrado.getEstado(), "A")) {
                String tokenCambioContrasenia = jwtTokenProvider.generateToken(service.findByNombreUsuario(usuario));
                return ResponseEntity.ok().body(new JwtAuthenticationResponse(tokenCambioContrasenia, usuarioEncontrado.getId(),
                        "CAMBIO_CONTRASENIA", "Para iniciar sesión por primera vez es necesario que cambie la contraseña que se le ha sido asignada por una segura"));
            }

            if (!Objects.equals(usuarioEncontrado.getEstado(), "A"))
                return ResponseEntity.badRequest().body(new ApiResponse(false,
                        "Su cuenta no se encuentra activa", "INACTIVA"));

            if (usuarioEncontrado.getRol().getId() == this.ROL_CLIENTE) {
                Cliente cliente = (Cliente) usuarioEncontrado;
                if (!cliente.tiene18OMasAnios()) return ResponseEntity.badRequest().body(new ApiResponse(false,
                        "El usuario no tiene la edad suficiente para utilizar el sistema", "MENOR_EDAD"));
            }

            if (generate2FA != null && generate2FA.equals("SI"))
                this.enviarCodigo2FA(usuarioEncontrado);

            String token = jwtTokenProvider.generateToken(service.findByNombreUsuario(usuario));
            return ResponseEntity.ok(new JwtAuthenticationResponse(token, usuarioEncontrado.getId(), "LOGIN_EXITOSO_2FA", "Usuario loggeado extosamente"));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Credenciales inválidas", "CREDENCIALES_INVALIDAS"));
        }
    }

    @PostMapping("/sendCodeByMail")
    public ResponseEntity<?> sendCodeByMail(@RequestBody SendCodeByMailRequest sendCodeByMailRequest) throws MessagingException, IOException {
        String mail = sendCodeByMailRequest.getCorreoElectronico();

        Usuario usuarioEncontrado = service.findUsuarioByCorreo(mail);

        // Validar credenciales
        if (usuarioEncontrado != null) {
            this.enviarCodigoCorreoElectronico2FA(usuarioEncontrado);
            return ResponseEntity.ok(new ApiResponse(true, "Operacion exitosa", "EXITO"));
        } else {
            return ResponseEntity.ok(new ApiResponse(true, "Operacion exitosa", "EXITO"));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        try {
            // Extrae los detalles del cambio de contraseña del request
            String usuario = changePasswordRequest.getUsuario();
            String contraseniaActual = changePasswordRequest.getContraseniaActual();
            String nuevaContrasenia = changePasswordRequest.getContraseniaNueva();

            // Valida que la contraseña actual sea correcta
            Usuario usuarioEncontrado = service.validarCredenciales(usuario, contraseniaActual);
            if (usuarioEncontrado == null) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, "Contraseña actual incorrecta", "CONTRASENIA_INCORRECTA"));
            }

            // Lógica para actualizar la contraseña
            service.actualizarContrasenia(usuarioEncontrado, nuevaContrasenia);

            return ResponseEntity.ok(new ApiResponse(true, "Contraseña cambiada con éxito", "CONTRASENIA_CAMBIADA"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/restart-password")
    public ResponseEntity<?> restartPassword(@RequestBody RestartPasswordRequest restartPasswordRequest) {
        try {
            String codigoDosFa = restartPasswordRequest.getCodigo2FA();
            String correoElectronico = restartPasswordRequest.getCorreoElectronico();
            String nuevaContrasenia = restartPasswordRequest.getContraseniaNueva();

            // Valida que la contraseña actual sea correcta
            Usuario usuarioEncontrado = service.findUsuarioByCorreo(correoElectronico);
            if (usuarioEncontrado == null) {
                return ResponseEntity.badRequest().build();
            }

            LocalDateTime now = LocalDateTime.now();

            Optional<VerificationCode> verificationCode = verificationCodeService.
                    findByUsernameAndCodeAndExpiresAtAfterAndUsedFalse(usuarioEncontrado.getNombreUsuario(),
                            codigoDosFa, now);

            if (verificationCode.isPresent()) {
                VerificationCode verificationCodeFounded = verificationCode.get();
                verificationCodeFounded.setUsed(true);
                this.verificationCodeService.saveVerificationCode(verificationCodeFounded);
            }else
                return ResponseEntity.ok(new ApiResponse(false, "El código incorrecto o ha expirado", "CODIGO_ERROR"));

            // Lógica para actualizar la contraseña
            service.actualizarContrasenia(usuarioEncontrado, nuevaContrasenia);

            return ResponseEntity.ok(new ApiResponse(true, "Contraseña cambiada con éxito", "CONTRASENIA_CAMBIADA"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody VerificationRequest verificationRequest) {
        String usuario = verificationRequest.getUsuario();
        String codigo = verificationRequest.getCodigo();
        LocalDateTime now = LocalDateTime.now();

        Optional<VerificationCode> verificationCode = verificationCodeService.
                findByUsernameAndCodeAndExpiresAtAfterAndUsedFalse(usuario, codigo, now);

        if (!verificationCode.isEmpty()) {
            VerificationCode verificationCodeFounded = verificationCode.get();
            verificationCodeFounded.setUsed(true);
            this.verificationCodeService.saveVerificationCode(verificationCodeFounded);

            return ResponseEntity.ok(new ApiResponse(true, "Código válido", "CODIGO_CORRECTO"));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Código de verificación incorrecto o expirado", "CODIGO_INVALIDO"));
        }
    }

    @PostMapping("/send-code")
    public ResponseEntity<?> sendCode(@RequestBody SendCodeRequest sendCodeRequest) throws MessagingException, IOException {
        String userName = sendCodeRequest.getUserName();
        Usuario usuario = service.findByNombreUsuario(userName);
        if (usuario != null) {
            this.enviarCodigo2FA(usuario);
            return ResponseEntity.ok(new ApiResponse(true, "Código enviado", "CODIGO_ENVIADO"));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Credenciales inválidas", "CREDENCIALES_INVALIDAS"));
        }
    }

    private void enviarCodigo2FA(Usuario usuario) throws MessagingException, IOException {
        String codigo2FA = service.generateCodigo2FA();
        VerificationCode verificationCode = this.generateVerificationCode(usuario, codigo2FA);
        String texto = "<p>El código 2FA para su acceso es el siguiente: </p>";
        this.enviarMailCodigo2FA(usuario, verificationCode, texto, "Código Inicio de Sesión");
    }

    private void enviarCodigoCorreoElectronico2FA(Usuario usuario) throws MessagingException, IOException {
        String codigo2FA = service.generateCodigo2FA();
        VerificationCode verificationCode = this.generateVerificationCode(usuario, codigo2FA);
        String texto = "<p>El código 2FA para el cambio de contraseña es el siguiente: </p>";
        this.enviarMailCodigo2FA(usuario, verificationCode, texto, "Código reinicio de contraseña");
    }

    private VerificationCode generateVerificationCode(Usuario usuario, String codigo2FA) {
        LocalDateTime createdAt = LocalDateTime.now(); // Fecha y hora actual
        LocalDateTime expiresAt = createdAt.plusMinutes(5); // Expira en 5 minuto

        VerificationCode verificationCode = new VerificationCode(usuario.getNombreUsuario(), codigo2FA, createdAt, expiresAt, false);
        return verificationCodeService.saveVerificationCode(verificationCode); // Guardar en la base de datos
    }

    private void enviarMailCodigo2FA(Usuario usuario, VerificationCode verificationCode, String texto, String asunto) throws MessagingException, IOException {
        String nombreCompleto = usuario.getNombres() + " " + usuario.getNombresDos() + " "
                + usuario.getApellidos() + " " + usuario.getApellidosDos();
        String nombreUsuario = usuario.getNombreUsuario();
        String codigo2FA = verificationCode.getCode(); // Obtener el código 2FA
        LocalDateTime fechaExpiracion = verificationCode.getExpiresAt(); // Obtener la fecha de expiración
        String fechaExpiracionFormateada = fechaExpiracion.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

        String textoMail = "<p><b>" + nombreCompleto + " [" + nombreUsuario + "]</b></p>" +
//                "<p>El código 2FA para su acceso es el siguiente: </p>" +
                texto +
                "<p><b>" + codigo2FA + "</b></p>" +
                "<p>Este código es válido hasta: <b>" + fechaExpiracionFormateada + "</b></p>";

//        this.mailService.sendHtmlEmail(usuario.getCorreoElectronico(), asunto, textoMail);
    }


}
