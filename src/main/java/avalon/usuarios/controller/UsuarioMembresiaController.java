package avalon.usuarios.controller;

import avalon.usuarios.model.pojo.ClienteMembresia;
import avalon.usuarios.model.request.CreateUsuarioMembresiaRequest;
import avalon.usuarios.model.request.UpdateUsuarioMembresiaRequest;
import avalon.usuarios.model.response.UsuariosMembresiaResponse;
import avalon.usuarios.service.ClienteMembresiaServiceImpl;
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

    private final ClienteMembresiaServiceImpl service;

    @PostMapping("/usuarioMembresias")
    public ResponseEntity<ClienteMembresia> createUsuarioMembresia(@RequestBody CreateUsuarioMembresiaRequest request) {
        try {
            ClienteMembresia result = service.createUsuarioMembresia(request);
            return result.getId() != null ? ResponseEntity.status(HttpStatus.CREATED).body(result) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/usuarioMembresias")
    public ResponseEntity<List<ClienteMembresia>> getUsuarioMembresias() {
        List<ClienteMembresia> clienteMembresias = service.getUsuarioMembresias();

        if (!clienteMembresias.isEmpty()) {
            return ResponseEntity.ok(clienteMembresias);
        } else {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @GetMapping("membresias/{membresiaId}/usuarioMembresias")
    public ResponseEntity<List<UsuariosMembresiaResponse>> getUsuarioMembresiasByMembresia(@PathVariable Long membresiaId) {
        List<UsuariosMembresiaResponse> usuarioMembresiasResponseList = service.getUsuariosMembresiasByMembresia(membresiaId);

        if (!usuarioMembresiasResponseList.isEmpty()) {
            return ResponseEntity.ok(usuarioMembresiasResponseList);
        } else {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @GetMapping("usuarios/{usuarioId}/usuarioMembresias")
    public ResponseEntity<List<UsuariosMembresiaResponse>> getUsuarioMembresiasByUsuario(@PathVariable Long usuarioId) {
        List<UsuariosMembresiaResponse> usuarioMembresiasResponseList = service.getUsuariosMembresiasByUsuario(usuarioId);

        if (!usuarioMembresiasResponseList.isEmpty()) {
            return ResponseEntity.ok(usuarioMembresiasResponseList);
        } else {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @GetMapping("/usuarioMembresias/{usuarioMembresiaId}")
    public ResponseEntity<ClienteMembresia> getUsuarioMembresia(@PathVariable Long usuarioMembresiaId) {
        ClienteMembresia clienteMembresia = service.getUsuarioMembresia(usuarioMembresiaId);

        if (clienteMembresia != null) {
            return ResponseEntity.ok(clienteMembresia);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/usuarioMembresias/{usuarioMembresiaId}")
    public ResponseEntity<ClienteMembresia> updateUsuarioMembresia(@PathVariable Long usuarioMembresiaId, @RequestBody UpdateUsuarioMembresiaRequest request) {
        ClienteMembresia clienteMembresia = service.getUsuarioMembresia(usuarioMembresiaId);

        if (clienteMembresia != null) {
            ClienteMembresia clienteMembresiaUpdate = service.updateUsuarioMembresia(clienteMembresia, request);
            return clienteMembresiaUpdate != null ? ResponseEntity.ok(clienteMembresiaUpdate) : ResponseEntity.badRequest().build();
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
