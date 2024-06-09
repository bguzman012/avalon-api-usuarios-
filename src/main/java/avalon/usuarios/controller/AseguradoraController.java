package avalon.usuarios.controller;

import avalon.usuarios.model.pojo.Aseguradora;
import avalon.usuarios.model.pojo.Usuario;
import avalon.usuarios.model.request.*;
import avalon.usuarios.model.response.CreateAseguradoraResponse;
import avalon.usuarios.service.AseguradoraServiceImpl;
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
public class AseguradoraController {

    private final AseguradoraServiceImpl service;

    @PostMapping("/aseguradoras")
    public ResponseEntity<Aseguradora> createAseguradora(@RequestBody CreateAseguradoraRequest request) {
        try {
            Aseguradora result = service.createAseguradora(request);
            return result.getId() != null ? ResponseEntity.status(HttpStatus.CREATED).body(result) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/aseguradoras")
    public ResponseEntity<List<CreateAseguradoraResponse>> getAseguradoras(@RequestParam(required = false) String estado) {
        List<CreateAseguradoraResponse> aseguradoras = service.getAseguradoraByEstado(estado);

        if (!aseguradoras.isEmpty()) {
            return ResponseEntity.ok(aseguradoras);
        } else {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @GetMapping("/usuarios/{usuarioId}/aseguradoras")
    public ResponseEntity<List<CreateAseguradoraResponse>> getAseguradorasByUsuario(@PathVariable Long usuarioId, @RequestParam(required = false) String estado) {
        List<CreateAseguradoraResponse> aseguradoras = service.getAseguradoraByUsuarioAndEstado(usuarioId, estado);

        if (!aseguradoras.isEmpty()) {
            return ResponseEntity.ok(aseguradoras);
        } else {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @GetMapping("/aseguradoras/{aseguradoraId}")
    public ResponseEntity<Aseguradora> getAseguradora(@PathVariable Long aseguradoraId) {
        Aseguradora aseguradora = service.getAseguradora(aseguradoraId);

        if (aseguradora != null) {
            return ResponseEntity.ok(aseguradora);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/aseguradoras/{aseguradoraId}")
    public ResponseEntity<Aseguradora> updateAseguradora(@PathVariable Long aseguradoraId, @RequestBody UpdateAseguradoraRequest request) {
        Aseguradora aseguradora = service.getAseguradora(aseguradoraId);

        if (aseguradora != null) {
            Aseguradora aseguradoraUpdate = service.updateAseguradora(aseguradora, request);
            return aseguradoraUpdate != null ? ResponseEntity.ok(aseguradoraUpdate) : ResponseEntity.badRequest().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/aseguradoras/{aseguradoraId}")
    public ResponseEntity<Aseguradora> partiallyUpdateAseguradora(@RequestBody PartiallyUpdateAseguradora request, @PathVariable Long aseguradoraId) {
        Aseguradora result = service.partiallyUpdateAseguradora(request, aseguradoraId);

        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/aseguradoras/{aseguradoraId}")
    public ResponseEntity<Void> deleteAseguradora(@PathVariable Long aseguradoraId) {
        service.deleteAseguradora(aseguradoraId);
        return ResponseEntity.noContent().build();
    }

}