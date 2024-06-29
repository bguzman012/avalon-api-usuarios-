package avalon.usuarios.controller;

import avalon.usuarios.model.pojo.Aseguradora;
import avalon.usuarios.model.pojo.ClientePoliza;
import avalon.usuarios.model.pojo.Reclamacion;
import avalon.usuarios.model.request.AseguradoraRequest;
import avalon.usuarios.model.request.PartiallyUpdateAseguradora;
import avalon.usuarios.model.request.PartiallyUpdateReclamacionRequest;
import avalon.usuarios.model.request.ReclamacionRequest;
import avalon.usuarios.service.AseguradoraService;
import avalon.usuarios.service.ClientesPolizaService;
import avalon.usuarios.service.ReclamacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ReclamoController {

    private final ReclamacionService service;
    @Autowired
    private ClientesPolizaService clientesPolizaService;

    @PostMapping("/reclamaciones")
    public ResponseEntity<Reclamacion> createReclamacion(@RequestPart("reclamacion") ReclamacionRequest request,
                                                         @RequestPart("fotoReclamo") MultipartFile fotoReclamo) {
        try {
            ClientePoliza clientePoliza = clientesPolizaService.getClientePoliza(request.getClientePolizaId()).orElseThrow(() -> new IllegalArgumentException("Cliente Poliza no encontrada"));
            Reclamacion reclamacion = this.mapToReclamacion(request, clientePoliza);

            if (!fotoReclamo.isEmpty()) {
                reclamacion.setFotoReclamo(fotoReclamo.getBytes());
            }

            reclamacion.setEstado("N");
            service.saveReclamacion(reclamacion);
            return reclamacion.getId() != null ? ResponseEntity.status(HttpStatus.CREATED).body(reclamacion) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/reclamaciones")
    public ResponseEntity<List<Reclamacion>> getReclamaciones(@RequestParam(required = false) String estado, @RequestParam(required = false) String clientePolizaId) {
        try {
            List<Reclamacion> reclamaciones = new ArrayList<>();

            if (!clientePolizaId.isBlank()){
                ClientePoliza clientePoliza = clientesPolizaService.getClientePoliza(Long.valueOf(clientePolizaId)).orElseThrow(() -> new IllegalArgumentException("Cliente Poliza no encontrada"));
                reclamaciones = service.getReclamacionesByClientePoliza(clientePoliza);
            }else if(estado.isBlank()){
                reclamaciones = service.getReclamaciones();
            }

            if (!reclamaciones.isEmpty()) {
                return ResponseEntity.ok(reclamaciones);
            } else {
                return ResponseEntity.ok(Collections.emptyList());
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/reclamaciones/{reclamacionId}")
    public ResponseEntity<Reclamacion> getReclamacion(@PathVariable Long reclamacionId) {
        Reclamacion reclamacion = service.getReclamacion(reclamacionId).orElseThrow(() -> new IllegalArgumentException("Reclamacion no encontrada"));

        if (reclamacion != null) {
            return ResponseEntity.ok(reclamacion);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/reclamaciones/{reclamacionId}")
    public ResponseEntity<Reclamacion> updateReclamacion(@PathVariable Long reclamacionId,
                                                         @RequestPart("reclamacion") ReclamacionRequest request,
                                                         @RequestPart("fotoReclamo") MultipartFile fotoReclamo) {
        try {
            ClientePoliza clientePoliza = clientesPolizaService.getClientePoliza(request.getClientePolizaId()).orElseThrow(() -> new IllegalArgumentException("Cliente Poliza no encontrada"));
            Reclamacion reclamacion = service.getReclamacion(reclamacionId).orElseThrow(() -> new IllegalArgumentException("Reclamacion no encontrada"));

            reclamacion.setRazon(request.getRazon());
            if (!fotoReclamo.isEmpty()) {
                reclamacion.setFotoReclamo(fotoReclamo.getBytes());
            }
            reclamacion.setEstado(request.getEstado());
            reclamacion.setClientePoliza(clientePoliza);
            service.saveReclamacion(reclamacion);

            return reclamacion.getId() != null ? ResponseEntity.ok(reclamacion) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @PatchMapping("/reclamaciones/{reclamacionId}")
    public ResponseEntity<Reclamacion> partiallyUpdateReclamacion(@RequestBody PartiallyUpdateReclamacionRequest request, @PathVariable Long reclamacionId) {
        Reclamacion result = service.partiallyUpdateReclamacion(request, reclamacionId);

        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/reclamaciones/{reclamacionId}")
    public ResponseEntity<Void> deleteReclamacion(@PathVariable Long reclamacionId) {
        service.deleteReclamacion(reclamacionId);
        return ResponseEntity.noContent().build();
    }

    private Reclamacion mapToReclamacion(ReclamacionRequest request, ClientePoliza clientePoliza) {
        return Reclamacion.builder()
                .razon(request.getRazon())
                .estado(request.getEstado())
                .clientePoliza(clientePoliza)
                .build();
    }

}