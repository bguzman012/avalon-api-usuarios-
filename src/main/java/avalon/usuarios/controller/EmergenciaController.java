package avalon.usuarios.controller;

import avalon.usuarios.model.pojo.Emergencia;
import avalon.usuarios.model.pojo.ClientePoliza;
import avalon.usuarios.model.pojo.Imagen;
import avalon.usuarios.model.request.EmergenciaRequest;
import avalon.usuarios.model.request.PartiallyUpdateEmergenciasRequest;
import avalon.usuarios.model.response.PaginatedResponse;
import avalon.usuarios.service.EmergenciaService;
import avalon.usuarios.service.ClientesPolizaService;
import avalon.usuarios.service.ImagenService;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EmergenciaController {

    private final EmergenciaService service;
    @Autowired
    private ClientesPolizaService clientesPolizaService;
    @Autowired
    private ImagenService imagenService;
    private String TOPICO = "IMAGEN_CITA_MEDICA";

    @PostMapping("/emergencias")
    public ResponseEntity<Emergencia> createEmergencia(@RequestPart("emergencia") EmergenciaRequest request,
                                                       @RequestPart("fotoEmergencia") MultipartFile fotoEmergencia) {
        try {
            ClientePoliza clientePoliza = clientesPolizaService.getClientePoliza(request.getClientePolizaId()).orElseThrow(() -> new IllegalArgumentException("Cliente Poliza no encontrada"));
            Emergencia emergencia = this.mapToEmergencia(request, clientePoliza);
            if (!fotoEmergencia.isEmpty()) {
                Imagen imagen = new Imagen(fotoEmergencia.getBytes(), this.TOPICO, request.getNombreDocumento());
                this.imagenService.saveImagen(imagen);
                emergencia.setImagenId(imagen.getId());
            }

            emergencia.setEstado("N");
            service.saveEmergencia(emergencia);
            return emergencia.getId() != null ? ResponseEntity.status(HttpStatus.CREATED).body(emergencia) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/emergencias")
    public ResponseEntity<PaginatedResponse<Emergencia>> getEmergencias(@RequestParam(required = false) String estado,
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

        Page<Emergencia> emergenciaPage = service.searchEmergencias(busqueda, estado, pageable, clientePoliza);

        List<Emergencia> emergencias = emergenciaPage.getContent();
        long totalRecords = emergenciaPage.getTotalElements();

        PaginatedResponse<Emergencia> response = new PaginatedResponse<>(emergencias, totalRecords);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/emergencias/{emergenciaId}")
    public ResponseEntity<Emergencia> getEmergenciaById(@PathVariable Long emergenciaId) {
        try {
            return service.getEmergencia(emergenciaId).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/emergencias/{emergenciaId}")
    public ResponseEntity<Emergencia> partiallyUpdateEmergencia(@RequestBody PartiallyUpdateEmergenciasRequest request, @PathVariable Long emergenciaId) {
        try {
            Emergencia emergencia = service.partiallyUpdateEmergencia(request, emergenciaId);
            return emergencia == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(emergencia);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/emergencias/{emergenciaId}")
    public ResponseEntity<Emergencia> updateEmergencia(@PathVariable Long emergenciaId,
                                                       @RequestPart("reclamacion") EmergenciaRequest request,
                                                       @RequestPart("fotoReclamo") MultipartFile fotoReclamo) {
        try {
            ClientePoliza clientePoliza = clientesPolizaService.getClientePoliza(request.getClientePolizaId()).orElseThrow(() -> new IllegalArgumentException("Cliente Poliza no encontrada"));
            Emergencia emergencia = service.getEmergencia(emergenciaId).orElseThrow(() -> new IllegalArgumentException("Emergencia no encontrada"));
            emergencia.setRazon(request.getRazon());

            if (emergencia.getImagenId() != null)
                this.imagenService.deleteImagen(emergencia.getImagenId());

            emergencia.setImagenId(null);

            if (!fotoReclamo.isEmpty()) {
                Imagen imagen = new Imagen(fotoReclamo.getBytes(), this.TOPICO, request.getNombreDocumento());
                this.imagenService.saveImagen(imagen);
                emergencia.setImagenId(imagen.getId());
            }

            emergencia.setEstado(request.getEstado());
            emergencia.setClientePoliza(clientePoliza);
            service.saveEmergencia(emergencia);

            return emergencia.getId() != null ? ResponseEntity.ok(emergencia) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/emergencias/{emergenciaId}")
    public ResponseEntity<Void> deleteEmergencia(@PathVariable Long emergenciaId) {
        try {
            service.deleteEmergencia(emergenciaId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private Emergencia mapToEmergencia(EmergenciaRequest request, ClientePoliza clientePoliza) {
        return Emergencia.builder()
                .razon(request.getRazon())
                .estado(request.getEstado())
                .clientePoliza(clientePoliza)
                .build();
    }
}
