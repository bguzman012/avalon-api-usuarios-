package avalon.usuarios.controller;

import avalon.usuarios.model.response.JwtAuthenticationResponse;
import avalon.usuarios.config.JwtTokenProvider;
import avalon.usuarios.model.pojo.Usuario;
import avalon.usuarios.model.request.LoginRequest;
import avalon.usuarios.model.response.ApiResponse;
import avalon.usuarios.service.UsuariosServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UsuariosServiceImpl service;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        String usuario = loginRequest.getUsuario();
        String contrasenia = loginRequest.getContrasenia();

        Usuario usuarioEncontrado = service.validarCredenciales(usuario,contrasenia);

        // Validar credenciales
        if (usuarioEncontrado != null) {
            // Generar y devolver el token JWT

            if (Objects.equals(usuarioEncontrado.getEstado(), "P")) return ResponseEntity.badRequest().body(new ApiResponse(false, "Su cuenta no se encuentra activa"));

            String token = jwtTokenProvider.generateToken(service.findByNombreUsuario(usuario));
            return ResponseEntity.ok(new JwtAuthenticationResponse(token, usuarioEncontrado.getId()));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Credenciales inválidas"));
        }
    }

}
