package avalon.usuarios.controller;

import avalon.usuarios.model.request.*;
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
public class UsuarioAseguradoraController {

    private final UsuarioAseguradoraServiceImpl service;

    @PostMapping("/usuarioAseguradoras")
    public ResponseEntity<List<UsuarioAseguradora>> createUsuarioAseguradora(@RequestBody CreateListUsuarioAseguradoraRequest request) {
        try {
            List<UsuarioAseguradora> result = service.createListUsuarioAseguradora(request);
            return result.get(0).getId() != null ? ResponseEntity.status(HttpStatus.CREATED).body(result) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/usuarioAseguradoras")
    public ResponseEntity<List<UsuarioAseguradora>> updateUsuariosAseguradoras(@RequestBody CreateListUsuarioAseguradoraRequest request, @RequestParam(required = false) Long usuarioId) {
        try {
            List<UsuarioAseguradora> result = service.updateListUsuariosAseguradoras(request, usuarioId);
            return result.get(0).getId() != null ? ResponseEntity.ok(result) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/usuarioAseguradoras")
    public ResponseEntity<List<UsuarioAseguradora>> getUsuarioAseguradoras() {
        List<UsuarioAseguradora> usuarioAseguradoras = service.getUsuarioAseguradoras();

        if (!usuarioAseguradoras.isEmpty()) {
            return ResponseEntity.ok(usuarioAseguradoras);
        } else {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @GetMapping("/aseguradoras/{aseguradoraId}/usuarioAseguradoras")
    public ResponseEntity<List<UsuarioAseguradora>> getUsuarioAseguradoraByAseguradoraAndRol(@PathVariable Long aseguradoraId,
                                                                                  @RequestParam(required = false) Long rolId,
                                                                                  @RequestParam(required = false) String estado) {
        List<UsuarioAseguradora> usuarioAseguradora = service.getUsuarioAseguradorasByAseguradoraAndRol(aseguradoraId, rolId, estado );

        if (usuarioAseguradora != null) {
            return ResponseEntity.ok(usuarioAseguradora);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/usuarioAseguradoras/{usuarioAseguradoraId}")
    public ResponseEntity<UsuarioAseguradora> getUsuarioAseguradora(@PathVariable Long usuarioAseguradoraId) {
        UsuarioAseguradora usuarioAseguradora = service.getUsuarioAseguradora(usuarioAseguradoraId);

        if (usuarioAseguradora != null) {
            return ResponseEntity.ok(usuarioAseguradora);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/usuarioAseguradoras/{usuarioAseguradoraId}")
    public ResponseEntity<Void> deleteUsuarioAseguradora(@PathVariable Long usuarioAseguradoraId) {
        service.deleteUsuarioAseguradora(usuarioAseguradoraId);
        return ResponseEntity.noContent().build();
    }

}
