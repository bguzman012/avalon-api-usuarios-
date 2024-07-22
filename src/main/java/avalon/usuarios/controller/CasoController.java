package avalon.usuarios.controller;

import avalon.usuarios.model.pojo.Caso;
import avalon.usuarios.model.pojo.ClientePoliza;
import avalon.usuarios.model.pojo.Imagen;
import avalon.usuarios.model.request.CasoRequest;
import avalon.usuarios.model.request.PartiallyUpdateCasoRequest;
import avalon.usuarios.model.response.PaginatedResponse;
import avalon.usuarios.service.CasoService;
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
public class CasoController {

    private final CasoService service;
    @Autowired
    private ClientesPolizaService clientesPolizaService;
    @Autowired
    private ImagenService imagenService;
    private String TOPICO = "IMAGEN_CITA_MEDICA";

    @PostMapping("/casos")
    public ResponseEntity<Caso> createCaso(@RequestPart("caso") CasoRequest request,
                                           @RequestPart("fotoCaso") MultipartFile fotoCaso) {
        try {
            ClientePoliza clientePoliza = clientesPolizaService.getClientePoliza(request.getClientePolizaId()).orElseThrow(() -> new IllegalArgumentException("Cliente Poliza no encontrada"));
            Caso caso = this.mapToCaso(request, clientePoliza);
            if (!fotoCaso.isEmpty()) {
                Imagen imagen = new Imagen(fotoCaso.getBytes(), this.TOPICO, request.getNombreDocumento());
                this.imagenService.saveImagen(imagen);
                caso.setImagenId(imagen.getId());
            }

            caso.setEstado("N");
            service.saveCaso(caso);
            return caso.getId() != null ? ResponseEntity.status(HttpStatus.CREATED).body(caso) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/casos")
    public ResponseEntity<PaginatedResponse<Caso>> getCasos(@RequestParam(required = false) String estado,
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

        Page<Caso> casoPage = service.searchCasos(busqueda, estado, pageable, clientePoliza);

        List<Caso> casos = casoPage.getContent();
        long totalRecords = casoPage.getTotalElements();

        PaginatedResponse<Caso> response = new PaginatedResponse<>(casos, totalRecords);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/casos/{casoId}")
    public ResponseEntity<Caso> getCasoById(@PathVariable Long casoId) {
        try {
            return service.getCaso(casoId).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/casos/{casoId}")
    public ResponseEntity<Caso> partiallyUpdateCaso(@RequestBody PartiallyUpdateCasoRequest request, @PathVariable Long casoId) {
        try {
            Caso caso = service.partiallyUpdateCaso(request, casoId);
            return caso == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(caso);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/casos/{casoId}")
    public ResponseEntity<Caso> updateCaso(@PathVariable Long casoId,
                                           @RequestPart("reclamacion") CasoRequest request,
                                           @RequestPart("fotoReclamo") MultipartFile fotoReclamo) {
        try {
            ClientePoliza clientePoliza = clientesPolizaService.getClientePoliza(request.getClientePolizaId()).orElseThrow(() -> new IllegalArgumentException("Cliente Poliza no encontrada"));
            Caso caso = service.getCaso(casoId).orElseThrow(() -> new IllegalArgumentException("Caso no encontrada"));
            caso.setRazon(request.getRazon());

            if (caso.getImagenId() != null)
                this.imagenService.deleteImagen(caso.getImagenId());

            caso.setImagenId(null);

            if (!fotoReclamo.isEmpty()) {
                Imagen imagen = new Imagen(fotoReclamo.getBytes(), this.TOPICO, request.getNombreDocumento());
                this.imagenService.saveImagen(imagen);
                caso.setImagenId(imagen.getId());
            }

            caso.setEstado(request.getEstado());
            caso.setClientePoliza(clientePoliza);
            service.saveCaso(caso);

            return caso.getId() != null ? ResponseEntity.ok(caso) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/casos/{casoId}")
    public ResponseEntity<Void> deleteCaso(@PathVariable Long casoId) {
        try {
            service.deleteCaso(casoId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private Caso mapToCaso(CasoRequest request, ClientePoliza clientePoliza) {
        return Caso.builder()
                .razon(request.getRazon())
                .estado(request.getEstado())
                .clientePoliza(clientePoliza)
                .build();
    }
}
