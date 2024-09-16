package avalon.usuarios.controller;
import avalon.usuarios.model.pojo.Usuario;
import avalon.usuarios.service.UsuariosService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class UsuarioController {

    private final UsuariosService service;

    @Autowired
    public UsuarioController(@Qualifier("usuariosServiceImpl") UsuariosService service) {
        this.service = service;
    }

    @GetMapping("/usuarios/{usuarioId}")
    public ResponseEntity<Usuario> getUsuario(@PathVariable Long usuarioId) {
        Usuario usuario = service.getUsuario(usuarioId);

        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
