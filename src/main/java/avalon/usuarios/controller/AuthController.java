package avalon.usuarios.controller;

import avalon.usuarios.model.pojo.Cliente;
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

    @Autowired
    public AuthController(@Qualifier("usuariosServiceImpl") UsuariosService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        String usuario = loginRequest.getUsuario();
        String contrasenia = loginRequest.getContrasenia();

        Usuario usuarioEncontrado = service.validarCredenciales(usuario,contrasenia);

        // Validar credenciales
        if (usuarioEncontrado != null) {
            // Generar y devolver el token JWT

            if (!Objects.equals(usuarioEncontrado.getEstado(), "A")) return ResponseEntity.badRequest().body(new ApiResponse(false, "Su cuenta no se encuentra activa"));

            if (usuarioEncontrado.getRol().getId() == 3 ) {
                Cliente cliente = (Cliente) usuarioEncontrado;
                if (!cliente.tiene18OMasAnios()) return ResponseEntity.badRequest().body(new ApiResponse(false, "El usuario no tiene la edad suficiente para utilizar el sistema"));
            }

            String token = jwtTokenProvider.generateToken(service.findByNombreUsuario(usuario));
            return ResponseEntity.ok(new JwtAuthenticationResponse(token, usuarioEncontrado.getId()));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Credenciales inválidas"));
        }
    }

}
