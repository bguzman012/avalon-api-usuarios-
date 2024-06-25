package avalon.usuarios.controller;

import avalon.usuarios.model.pojo.Poliza;
import avalon.usuarios.model.request.CreatePolizaRequest;
import avalon.usuarios.model.request.UpdatePolizaRequest;
import avalon.usuarios.service.PolizasServiceImpl;
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
public class PolizaController {

    private final PolizasServiceImpl service;

    @PostMapping("/polizas")
    public ResponseEntity<Poliza> create(@RequestBody CreatePolizaRequest request) {
        try {
            Poliza result = service.createPoliza(request);
            return result.getId() != null ? ResponseEntity.status(HttpStatus.CREATED).body(result) : ResponseEntity.badRequest().build();
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
        Poliza poliza = service.getPoliza(polizaId);

        if (poliza != null) {
            return ResponseEntity.ok(poliza);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/polizas/{polizaId}")
    public ResponseEntity<Poliza> updatePoliza(@PathVariable Long polizaId, @RequestBody UpdatePolizaRequest request) {
        Poliza poliza = service.getPoliza(polizaId);

        if (poliza != null) {
            Poliza polizaUpdate = service.updatePoliza(poliza, request);
            return polizaUpdate != null ? ResponseEntity.ok(polizaUpdate) : ResponseEntity.badRequest().build();
        } else {
            return ResponseEntity.notFound().build();
        }
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

}