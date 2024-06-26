package avalon.usuarios.controller;

import avalon.usuarios.model.pojo.*;
import avalon.usuarios.model.request.PolizaRequest;
import avalon.usuarios.service.AseguradoraService;
import avalon.usuarios.service.PolizaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PolizaController {

    private final PolizaService service;
    @Autowired
    private AseguradoraService aseguradoraService;

    @PostMapping("/polizas")
    public ResponseEntity<Poliza> create(@RequestBody PolizaRequest request) {
        try {
            Poliza poliza = this.mapToPoliza(request, new Poliza());
            poliza.setEstado("A");
            Poliza polizaCreated = this.service.savePoliza(poliza);
            return polizaCreated.getId() != null ? ResponseEntity.status(HttpStatus.CREATED).body(polizaCreated) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/polizas")
    public ResponseEntity<List<Poliza>> getPolizas() {
        List<Poliza> polizas = service.getPolizas();

        if (!polizas.isEmpty()) {
            return ResponseEntity.ok(polizas);
        } else {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @GetMapping("/aseguradoras/{aseguradoraId}/polizas")
    public ResponseEntity<List<Poliza>> getPolizasByAseguradora(@PathVariable Long aseguradoraId) {
        List<Poliza> polizas = service.getPolizasByAseguradora(aseguradoraId);

        if (!polizas.isEmpty()) {
            return ResponseEntity.ok(polizas);
        } else {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }


//    @GetMapping("/usuarios/{usuarioId}/aseguradoras")
//    public ResponseEntity<List<CreateAseguradoraResponse>> getMembresiasByUsuario(@PathVariable Long usuarioId, @RequestParam(required = false) String estado) {
//        List<CreateAseguradoraResponse> aseguradoras = service.getAseguradoraByUsuarioAndEstado(usuarioId, estado);
//
//        if (!aseguradoras.isEmpty()) {
//            return ResponseEntity.ok(aseguradoras);
//        } else {
//            return ResponseEntity.ok(Collections.emptyList());
//        }
//    }

    @GetMapping("/polizas/{polizaId}")
    public ResponseEntity<Poliza> getPoliza(@PathVariable Long polizaId) {
        Poliza poliza = service.getPoliza(polizaId).orElseThrow(() -> new IllegalArgumentException("Poliza no encontrada"));

        if (poliza != null) {
            return ResponseEntity.ok(poliza);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/polizas/{polizaId}")
    public ResponseEntity<Poliza> updatePoliza(@PathVariable Long polizaId, @RequestBody PolizaRequest request) {
        Poliza poliza = service.getPoliza(polizaId).orElseThrow(() -> new IllegalArgumentException("Poliza no encontrada"));
        Poliza polizaMapped = this.mapToPoliza(request, poliza);
        this.service.savePoliza(polizaMapped);

        return polizaMapped != null ? ResponseEntity.ok(polizaMapped) : ResponseEntity.badRequest().build();
    }

//    @PatchMapping("/aseguradoras/{aseguradoraId}")
//    public ResponseEntity<Aseguradora> partiallyUpdateAseguradora(@RequestBody PartiallyUpdateAseguradora request, @PathVariable Long aseguradoraId) {
//        Aseguradora result = service.partiallyUpdateAseguradora(request, aseguradoraId);
//
//        if (result != null) {
//            return ResponseEntity.ok(result);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }

    @DeleteMapping("/polizas/{polizaId}")
    public ResponseEntity<Void> deletePoliza(@PathVariable Long polizaId) {
        service.deletePoliza(polizaId);
        return ResponseEntity.noContent().build();
    }

    private Poliza mapToPoliza(PolizaRequest request, Poliza polizaReference) {
        Aseguradora aseguradora = this.aseguradoraService.getAseguradora(request.getAseguradoraId()).orElseThrow(() -> new IllegalArgumentException("Aseguradora no encontrado"));

        polizaReference.setNombre(request.getNombre());
        polizaReference.setDescripcion(request.getDescripcion());
        polizaReference.setVigenciaMeses(request.getVigenciaMeses());
        polizaReference.setAseguradora(aseguradora);
        return polizaReference;
    }

}