package avalon.usuarios.controller;

import avalon.usuarios.model.pojo.*;
import avalon.usuarios.model.request.AseguradoraRequest;
import avalon.usuarios.model.request.PartiallyUpdateAseguradora;
import avalon.usuarios.model.request.PartiallyUpdateReclamacionRequest;
import avalon.usuarios.model.request.ReclamacionRequest;
import avalon.usuarios.model.response.PaginatedResponse;
import avalon.usuarios.service.AseguradoraService;
import avalon.usuarios.service.ClientesPolizaService;
import avalon.usuarios.service.ImagenService;
import avalon.usuarios.service.ReclamacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    @Autowired
    private ImagenService imagenService;
    private String TOPICO = "IMAGEN_RECLAMO";

    @PostMapping("/reclamaciones")
    public ResponseEntity<Reclamacion> createReclamacion(@RequestPart("reclamacion") ReclamacionRequest request,
                                                         @RequestPart("fotoReclamo") MultipartFile fotoReclamo) {
        try {
            ClientePoliza clientePoliza = clientesPolizaService.getClientePoliza(request.getClientePolizaId()).orElseThrow(() -> new IllegalArgumentException("Cliente Poliza no encontrada"));
            Reclamacion reclamacion = this.mapToReclamacion(request, clientePoliza);
            if (!fotoReclamo.isEmpty()) {
                Imagen imagen = new Imagen(fotoReclamo.getBytes(), this.TOPICO, request.getNombreDocumento());
                this.imagenService.saveImagen(imagen);
                reclamacion.setImagenId(imagen.getId());
            }

            reclamacion.setEstado("N");
            service.saveReclamacion(reclamacion);
            return reclamacion.getId() != null ? ResponseEntity.status(HttpStatus.CREATED).body(reclamacion) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/reclamaciones")
    public ResponseEntity<PaginatedResponse<Reclamacion>> getReclamaciones(@RequestParam(required = false) String estado,
                                                                           @RequestParam(required = false) String clientePolizaId,
                                                                           @RequestParam(required = false) String busqueda,
                                                                           @RequestParam(defaultValue = "0") int page,
                                                                           @RequestParam(defaultValue = "10") int size,
                                                                           @RequestParam(defaultValue = "createdDate") String sortField,
                                                                           @RequestParam(defaultValue = "desc") String sortOrder) {

        ClientePoliza clientePoliza = null;

        if (!clientePolizaId.isBlank()) {
            clientePoliza = clientesPolizaService.getClientePoliza(Long.valueOf(clientePolizaId))
                    .orElseThrow(() -> new IllegalArgumentException("Cliente Poliza no encontrada"));
        }

        Sort sort = sortOrder.equalsIgnoreCase("desc") ? Sort.by(sortField).descending() : Sort.by(sortField).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Reclamacion> reclamacionPage = service.searchReclamaciones(busqueda, estado, pageable, clientePoliza);

        List<Reclamacion> reclamaciones = reclamacionPage.getContent();
        long totalRecords = reclamacionPage.getTotalElements();

        PaginatedResponse<Reclamacion> response = new PaginatedResponse<>(reclamaciones, totalRecords);
        return ResponseEntity.ok(response);
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

            if (reclamacion.getImagenId() != null)
                this.imagenService.deleteImagen(reclamacion.getImagenId());

            reclamacion.setImagenId(null);

            if (!fotoReclamo.isEmpty()) {
                Imagen imagen = new Imagen(fotoReclamo.getBytes(), this.TOPICO, request.getNombreDocumento());
                this.imagenService.saveImagen(imagen);
                reclamacion.setImagenId(imagen.getId());
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