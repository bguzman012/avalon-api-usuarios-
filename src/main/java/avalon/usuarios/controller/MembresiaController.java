package avalon.usuarios.controller;

import avalon.usuarios.model.pojo.Aseguradora;
import avalon.usuarios.model.pojo.Membresia;
import avalon.usuarios.model.request.*;
import avalon.usuarios.model.response.CreateAseguradoraResponse;
import avalon.usuarios.service.AseguradoraServiceImpl;
import avalon.usuarios.service.MembresiaServiceImpl;
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
public class MembresiaController {

    private final MembresiaServiceImpl service;

    @PostMapping("/membresias")
    public ResponseEntity<Membresia> createMembresia(@RequestBody CreateMembresiaRequest request) {
        try {
            Membresia result = service.createMembresia(request);
            return result.getId() != null ? ResponseEntity.status(HttpStatus.CREATED).body(result) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/membresias")
    public ResponseEntity<List<Membresia>> getMembresias(@RequestParam(required = false) String estado) {
        List<Membresia> aseguradoras = service.getMembresiasByEstado(estado);

        if (!aseguradoras.isEmpty()) {
            return ResponseEntity.ok(aseguradoras);
        } else {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @GetMapping("/aseguradoras/{aseguradoraId}/membresias")
    public ResponseEntity<List<Membresia>> getMembresiasByAseguradora(@PathVariable Long aseguradoraId) {
        List<Membresia> aseguradoras = service.getMembresiasByAseguradora(aseguradoraId);

        if (!aseguradoras.isEmpty()) {
            return ResponseEntity.ok(aseguradoras);
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

    @GetMapping("/membresias/{membresiaId}")
    public ResponseEntity<Membresia> getMembresia(@PathVariable Long membresiaId) {
        Membresia membresia = service.getMembresia(membresiaId);

        if (membresia != null) {
            return ResponseEntity.ok(membresia);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/membresias/{membresiaId}")
    public ResponseEntity<Membresia> updateMembresia(@PathVariable Long membresiaId, @RequestBody UpdateMembresiaRequest request) {
        Membresia membresia = service.getMembresia(membresiaId);

        if (membresia != null) {
            Membresia membresiaUpdate = service.updateMembresia(membresia, request);
            return membresiaUpdate != null ? ResponseEntity.ok(membresiaUpdate) : ResponseEntity.badRequest().build();
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

    @DeleteMapping("/membresias/{membresiaId}")
    public ResponseEntity<Void> deleteMembresia(@PathVariable Long membresiaId) {
        service.deleteMembresia(membresiaId);
        return ResponseEntity.noContent().build();
    }

}