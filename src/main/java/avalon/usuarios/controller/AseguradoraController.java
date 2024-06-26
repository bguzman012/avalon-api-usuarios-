package avalon.usuarios.controller;

import avalon.usuarios.model.pojo.Aseguradora;
import avalon.usuarios.model.request.*;
import avalon.usuarios.model.response.CreateAseguradoraResponse;
import avalon.usuarios.service.AseguradoraService;
import avalon.usuarios.service.AseguradoraServiceImpl;
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

    private final AseguradoraService service;

    @PostMapping("/aseguradoras")
    public ResponseEntity<Aseguradora> createAseguradora(@RequestBody AseguradoraRequest request) {
        try {
            Aseguradora aseguradora = this.mapToAseguradora(request);
            aseguradora.setEstado("A");
            service.createAseguradora(aseguradora);
            return aseguradora.getId() != null ? ResponseEntity.status(HttpStatus.CREATED).body(aseguradora) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/aseguradoras")
    public ResponseEntity<List<Aseguradora>> getAseguradoras(@RequestParam(required = false) String estado) {
        List<Aseguradora> aseguradoras = service.getAseguradoraByEstado(estado);

        if (!aseguradoras.isEmpty()) {
            return ResponseEntity.ok(aseguradoras);
        } else {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

//    @GetMapping("/usuarios/{usuarioId}/aseguradoras")
//    public ResponseEntity<List<CreateAseguradoraResponse>> getAseguradorasByUsuario(@PathVariable Long usuarioId, @RequestParam(required = false) String estado) {
//        List<CreateAseguradoraResponse> aseguradoras = service.getAseguradoraByUsuarioAndEstado(usuarioId, estado);
//
//        if (!aseguradoras.isEmpty()) {
//            return ResponseEntity.ok(aseguradoras);
//        } else {
//            return ResponseEntity.ok(Collections.emptyList());
//        }
//    }

    @GetMapping("/aseguradoras/{aseguradoraId}")
    public ResponseEntity<Aseguradora> getAseguradora(@PathVariable Long aseguradoraId) {
        Aseguradora aseguradora = service.getAseguradora(aseguradoraId).orElseThrow(() -> new IllegalArgumentException("Aseguradora no encontrada"));

        if (aseguradora != null) {
            return ResponseEntity.ok(aseguradora);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/aseguradoras/{aseguradoraId}")
    public ResponseEntity<Aseguradora> updateAseguradora(@PathVariable Long aseguradoraId, @RequestBody AseguradoraRequest request) {
        Aseguradora aseguradora = service.getAseguradora(aseguradoraId).orElseThrow(() -> new IllegalArgumentException("Aseguradora no encontrada"));
        aseguradora.setNombre(request.getNombre());
        aseguradora.setCorreoElectronico(request.getCorreoElectronico());
        service.createAseguradora(aseguradora);

        return aseguradora != null ? ResponseEntity.ok(aseguradora) : ResponseEntity.badRequest().build();
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

    private Aseguradora mapToAseguradora(AseguradoraRequest request) {
        return Aseguradora.builder()
                .nombre(request.getNombre())
                .correoElectronico(request.getCorreoElectronico())
                .build();
    }

}