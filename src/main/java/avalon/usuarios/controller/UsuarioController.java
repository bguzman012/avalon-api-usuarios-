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

//    @PostMapping("/usuarios")
//    public ResponseEntity<Usuario> createUsuario(@RequestBody UsuarioRequest request) {
//        try {
//            Usuario result = service.createUsuario(request);
//            return result.getId() != null ? ResponseEntity.status(HttpStatus.CREATED).body(result) : ResponseEntity.badRequest().build();
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }
//
//    @GetMapping("/usuarios")
//    public ResponseEntity<List<Usuario>> getUsuarios() {
//        List<Usuario> usuarios = service.getUsuarios();
//
//        if (!usuarios.isEmpty()) {
//            return ResponseEntity.ok(usuarios);
//        } else {
//            return ResponseEntity.ok(Collections.emptyList());
//        }
//    }
//
    @GetMapping("/usuarios/{usuarioId}")
    public ResponseEntity<Usuario> getUsuario(@PathVariable Long usuarioId) {
        Usuario usuario = service.getUsuario(usuarioId);

        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
//
//    @PatchMapping("/usuarios/{usuarioId}")
//    public ResponseEntity<Usuario> partiallyUpdateUsuario(@RequestBody PartiallyUpdateUsuario request, @PathVariable Long usuarioId) {
//        Usuario result = service.partiallyUpdateUsuario(request, usuarioId);
//
//        if (result != null) {
//            return ResponseEntity.ok(result);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }
//
//    @PutMapping("/usuarios/{usuarioId}")
//    public ResponseEntity<Usuario> updateUsuario(@PathVariable Long usuarioId, @RequestBody UpdateUsuarioRequest request) {
//        Usuario usuario = service.getUsuario(usuarioId);
//
//        if (usuario != null) {
//            Usuario usuarioUpdate = service.updateUsuario(usuario, request);
//            return usuarioUpdate != null ? ResponseEntity.ok(usuarioUpdate) : ResponseEntity.badRequest().build();
//        }
//        else {
//            return ResponseEntity.notFound().build();
//        }
//
//    }
//
//    @GetMapping("/roles/{rolId}/usuarios")
//    public ResponseEntity<List<Usuario>> getUsuariosByRol(@PathVariable Long rolId, @RequestParam(required = false) String estado) {
//        List<Usuario> usuarios = service.getUsuariosByRolAndEstado(rolId, estado);
//
//        if (!usuarios.isEmpty()) {
//            return ResponseEntity.ok(usuarios);
//        } else {
//            return ResponseEntity.ok(Collections.emptyList());
//        }
//    }



}
