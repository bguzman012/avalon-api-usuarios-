package avalon.usuarios.controller;

import avalon.usuarios.model.pojo.Usuario;
import avalon.usuarios.model.request.CreateUsuarioRequest;
import avalon.usuarios.model.request.UpdateEstadoUsuario;
import avalon.usuarios.model.request.UpdateUsuarioRequest;
import avalon.usuarios.service.UsuariosServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UsuarioController {

    private final UsuariosServiceImpl service;

    @PostMapping("/usuarios")
    public ResponseEntity<Usuario> createUsuario(@RequestBody CreateUsuarioRequest request) {
        try {
            Usuario result = service.createUsuario(request);
            return result.getId() != null ? ResponseEntity.status(HttpStatus.CREATED).body(result) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/usuarios")
    public ResponseEntity<List<Usuario>> getUsuarios() {
        List<Usuario> usuarios = service.getUsuarios();

        if (!usuarios.isEmpty()) {
            return ResponseEntity.ok(usuarios);
        } else {
            return ResponseEntity.ok(Collections.emptyList());
        }
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

    @PatchMapping("/usuarios/{usuarioId}")
    public ResponseEntity<Usuario> partiallyUpdateUsuario(@RequestBody UpdateEstadoUsuario request, @PathVariable Long usuarioId) {
        Usuario result = service.partiallyUpdateEstadoUsuario(request, usuarioId);

        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/usuarios/{usuarioId}")
    public ResponseEntity<Usuario> updateUsuario(@PathVariable Long usuarioId, @RequestBody UpdateUsuarioRequest request) {
        Usuario usuario = service.getUsuario(usuarioId);

        if (usuario != null) {
            Usuario usuarioUpdate = service.updateUsuario(usuario, request);
            return usuarioUpdate != null ? ResponseEntity.ok(usuarioUpdate) : ResponseEntity.badRequest().build();
        }
        else {
            return ResponseEntity.notFound().build();
        }

    }

    @GetMapping("/roles/{rolId}/usuarios")
    public ResponseEntity<List<Usuario>> getUsuariosByRol(@PathVariable Long rolId) {
        List<Usuario> usuarios = service.getUsuariosByRol(rolId);

        if (!usuarios.isEmpty()) {
            return ResponseEntity.ok(usuarios);
        } else {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

}
