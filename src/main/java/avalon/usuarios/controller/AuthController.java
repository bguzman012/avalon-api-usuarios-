package avalon.usuarios.controller;

import avalon.usuarios.model.pojo.Cliente;
import avalon.usuarios.model.request.ChangePasswordRequest;
import avalon.usuarios.model.response.JwtAuthenticationResponse;
import avalon.usuarios.config.JwtTokenProvider;
import avalon.usuarios.model.pojo.Usuario;
import avalon.usuarios.model.request.LoginRequest;
import avalon.usuarios.model.response.ApiResponse;
import avalon.usuarios.service.UsuariosService;
import avalon.usuarios.service.UsuariosServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@Slf4j
public class AuthController {

    private final UsuariosService service;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    private final int ROL_CLIENTE = 3;

    @Autowired
    public AuthController(@Qualifier("usuariosServiceImpl") UsuariosService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
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

            String token = jwtTokenProvider.generateToken(service.findByNombreUsuario(usuario));
            return ResponseEntity.ok(new JwtAuthenticationResponse(token, usuarioEncontrado.getId(), "LOGIN_EXITOSO", "Usuario loggeado extosamente"));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Credenciales inválidas", "CREDENCIALES_INVALIDAS"));
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

}
