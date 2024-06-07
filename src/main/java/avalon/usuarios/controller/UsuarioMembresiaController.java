package avalon.usuarios.controller;

import avalon.usuarios.model.pojo.UsuarioAseguradora;
import avalon.usuarios.model.pojo.UsuarioMembresia;
import avalon.usuarios.model.request.CreateUsuarioAseguradoraRequest;
import avalon.usuarios.model.request.CreateUsuarioMembresiaRequest;
import avalon.usuarios.model.request.UpdateUsuarioMembresiaRequest;
import avalon.usuarios.service.UsuarioAseguradoraServiceImpl;
import avalon.usuarios.service.UsuarioMembresiaServiceImpl;
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
public class UsuarioMembresiaController {

    private final UsuarioMembresiaServiceImpl service;

    @PostMapping("/usuarioMembresias")
    public ResponseEntity<UsuarioMembresia> createUsuarioMembresia(@RequestBody CreateUsuarioMembresiaRequest request) {
        try {
            UsuarioMembresia result = service.createUsuarioMembresia(request);
            return result.getId() != null ? ResponseEntity.status(HttpStatus.CREATED).body(result) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/usuarioMembresias")
    public ResponseEntity<List<UsuarioMembresia>> getUsuarioMembresias() {
        List<UsuarioMembresia> usuarioMembresias = service.getUsuarioMembresias();

        if (!usuarioMembresias.isEmpty()) {
            return ResponseEntity.ok(usuarioMembresias);
        } else {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @GetMapping("/usuarioMembresias/{usuarioMembresiaId}")
    public ResponseEntity<UsuarioMembresia> getUsuarioMembresia(@PathVariable Long usuarioMembresiaId) {
        UsuarioMembresia usuarioMembresia = service.getUsuarioMembresia(usuarioMembresiaId);

        if (usuarioMembresia != null) {
            return ResponseEntity.ok(usuarioMembresia);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/usuarioMembresias/{usuarioMembresiaId}")
    public ResponseEntity<UsuarioMembresia> updateUsuarioMembresia(@PathVariable Long usuarioMembresiaId, @RequestBody UpdateUsuarioMembresiaRequest request) {
        UsuarioMembresia usuarioMembresia = service.getUsuarioMembresia(usuarioMembresiaId);

        if (usuarioMembresia != null) {
            UsuarioMembresia usuarioMembresiaUpdate = service.updateUsuarioMembresia(usuarioMembresia, request);
            return usuarioMembresiaUpdate != null ? ResponseEntity.ok(usuarioMembresiaUpdate) : ResponseEntity.badRequest().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/usuarioMembresias/{usuarioMembresiaId}")
    public ResponseEntity<Void> deleteUsuarioMembresia(@PathVariable Long usuarioMembresiaId) {
        service.deleteUsuarioMembresia(usuarioMembresiaId);
        return ResponseEntity.noContent().build();
    }

}
