package avalon.usuarios.controller;

import avalon.usuarios.model.request.CreateUsuAseguradoraUsuMembresiaRequest;
import avalon.usuarios.model.request.UpdateUsuAseguradoraUsuMembresiaRequest;
import avalon.usuarios.service.UsuAseguradoraUsuMembresiaServiceImpl;
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
public class UsuAseguradoraUsuMembresiaController {

    private final UsuAseguradoraUsuMembresiaServiceImpl service;

    @PostMapping("/usuAseguradorasUsuMembresias")
    public ResponseEntity<UsuAseguradoraUsuMembresia> createUsuAseguradoraUsuMembresia(@RequestBody CreateUsuAseguradoraUsuMembresiaRequest request) {
        try {
            UsuAseguradoraUsuMembresia result = service.createUsuAseguradoraUsuMembresia(request);
            return result.getId() != null ? ResponseEntity.status(HttpStatus.CREATED).body(result) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/usuAseguradorasUsuMembresias")
    public ResponseEntity<List<UsuAseguradoraUsuMembresia>> getUsuAseguradoraUsuMembresias() {
        List<UsuAseguradoraUsuMembresia> usuAseguradoraUsuMembresia = service.getUsuAseguradoraUsuMembresias();

        if (!usuAseguradoraUsuMembresia.isEmpty()) {
            return ResponseEntity.ok(usuAseguradoraUsuMembresia);
        } else {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @GetMapping("/usuAseguradorasUsuMembresias/{usuAseguradoraUsuMembresiaId}")
    public ResponseEntity<UsuAseguradoraUsuMembresia> getUsuAseguradoraUsuMembresia(@PathVariable Long usuAseguradoraUsuMembresiaId) {
        UsuAseguradoraUsuMembresia usuAseguradoraUsuMembresia = service.getUsuAseguradoraUsuMembresia(usuAseguradoraUsuMembresiaId);

        if (usuAseguradoraUsuMembresia != null) {
            return ResponseEntity.ok(usuAseguradoraUsuMembresia);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/usuAseguradorasUsuMembresias/{usuAseguradoraUsuMembresiaId}")
    public ResponseEntity<UsuAseguradoraUsuMembresia> updateUsuAseguradoraUsuMembresia(@PathVariable Long usuAseguradoraUsuMembresiaId, @RequestBody UpdateUsuAseguradoraUsuMembresiaRequest request) {
        UsuAseguradoraUsuMembresia usuAseguradoraUsuMembresia = service.getUsuAseguradoraUsuMembresia(usuAseguradoraUsuMembresiaId);

        if (usuAseguradoraUsuMembresia != null) {
            UsuAseguradoraUsuMembresia usuAseguradoraUsuMembresiaUpdate = service.updateUsuAseguradoraUsuMembresia(usuAseguradoraUsuMembresia, request);
            return usuAseguradoraUsuMembresiaUpdate != null ? ResponseEntity.ok(usuAseguradoraUsuMembresiaUpdate) : ResponseEntity.badRequest().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/usuAseguradorasUsuMembresias/{usuAseguradoraUsuMembresiaId}")
    public ResponseEntity<Void> deleteUsuAseguradoraUsuMembresia(@PathVariable Long usuAseguradoraUsuMembresiaId) {
        service.deleteUsuAseguradoraUsuMembresia(usuAseguradoraUsuMembresiaId);
        return ResponseEntity.noContent().build();
    }

}
