package avalon.usuarios.controller;

import avalon.usuarios.model.pojo.*;
import avalon.usuarios.model.request.*;
import avalon.usuarios.model.response.PaginatedResponse;
import avalon.usuarios.service.*;
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
    private MedicoCentroMedicoAseguradoraService medicoCentroMedicoAseguradoraService;
    @Autowired
    private CasoService casoService;
    @Autowired
    private ImagenService imagenService;
    private String TOPICO = "IMAGEN_RECLAMO";

    @PostMapping("/reclamaciones")
    public ResponseEntity<Reclamacion> createReclamacion(@RequestPart("reclamacion") ReclamacionRequest request,
                                                         @RequestPart("fotoReclamo") MultipartFile fotoReclamo) {
        try {
            request.setEstado("N");
            Reclamacion reclamacion = this.mapToReclamacion(request, new Reclamacion());
            if (!fotoReclamo.isEmpty()) {
                Imagen imagen = new Imagen(fotoReclamo.getBytes(), this.TOPICO, request.getNombreDocumento());
                this.imagenService.saveImagen(imagen);
                reclamacion.setImagenId(imagen.getId());
            }

            service.saveReclamacion(reclamacion);
            return reclamacion.getId() != null ? ResponseEntity.status(HttpStatus.CREATED).body(reclamacion) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/reclamaciones")
    public ResponseEntity<PaginatedResponse<Reclamacion>> getReclamaciones(@RequestParam(required = false) String estado,
                                                                           @RequestParam(required = false) String clientePolizaId,
                                                                           @RequestParam(required = false) String casoId,
                                                                           @RequestParam(required = false) String busqueda,
                                                                           @RequestParam(defaultValue = "0") int page,
                                                                           @RequestParam(defaultValue = "10") int size,
                                                                           @RequestParam(defaultValue = "createdDate") String sortField,
                                                                           @RequestParam(defaultValue = "desc") String sortOrder) {

        ClientePoliza clientePoliza = null;
        Caso caso = null;

        if (clientePolizaId != null && !clientePolizaId.isBlank()) {
            clientePoliza = clientesPolizaService.getClientePoliza(Long.valueOf(clientePolizaId))
                    .orElseThrow(() -> new IllegalArgumentException("Cliente Poliza no encontrada"));
        }

        if (casoId != null && !casoId.isBlank()) {
            caso = casoService.getCaso(Long.valueOf(casoId))
                    .orElseThrow(() -> new IllegalArgumentException("Caso no encontrada"));
        }

        Sort sort = sortOrder.equalsIgnoreCase("desc") ? Sort.by(sortField).descending() : Sort.by(sortField).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Reclamacion> reclamacionPage = service.searchReclamaciones(busqueda, estado, pageable, clientePoliza, caso);

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
                                                         @RequestPart(value="fotoReclamo", required = false) MultipartFile fotoReclamo) {
        try {
            Reclamacion reclamacion = service.getReclamacion(reclamacionId).orElseThrow(() -> new IllegalArgumentException("Reclamacion no encontrada"));
            Reclamacion reclamacionMapped = this.mapToReclamacion(request, reclamacion);


            if (reclamacionMapped.getImagenId() != null && fotoReclamo != null){
                this.imagenService.deleteImagen(reclamacion.getImagenId());
                reclamacionMapped.setImagenId(null);
            }

            if (fotoReclamo != null && !fotoReclamo.isEmpty()) {
                Imagen imagen = new Imagen(fotoReclamo.getBytes(), this.TOPICO, request.getNombreDocumento());
                this.imagenService.saveImagen(imagen);
                reclamacionMapped.setImagenId(imagen.getId());
            }

            service.saveReclamacion(reclamacionMapped);
            return reclamacionMapped.getId() != null ? ResponseEntity.ok(reclamacionMapped) : ResponseEntity.badRequest().build();
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

    private Reclamacion mapToReclamacion(ReclamacionRequest request, Reclamacion reclamacion) {
        ClientePoliza clientePoliza = clientesPolizaService.getClientePoliza(request.getClientePolizaId()).orElseThrow(() -> new IllegalArgumentException("Cliente Poliza no encontrada"));
        MedicoCentroMedicoAseguradora medicoCentroMedicoAseguradora = medicoCentroMedicoAseguradoraService.getMedicoCentroMedicoAseguradora(
                request.getMedicoCentroMedicoAseguradoraId()).orElseThrow(() -> new IllegalArgumentException("Centro Médico no encontrado"));
        Caso caso = casoService.getCaso(request.getCasoId()).orElseThrow(() -> new IllegalArgumentException("Caso no encontrado"));

        reclamacion.setCaso(caso);
        reclamacion.setEstado(request.getEstado());
        reclamacion.setFechaServicio(request.getFechaServicio());
        reclamacion.setClientePoliza(clientePoliza);
        reclamacion.setMedicoCentroMedicoAseguradora(medicoCentroMedicoAseguradora);
        reclamacion.setTipoAdm(TipoAdm.valueOf(request.getTipoAdm().toUpperCase()));
        reclamacion.setPadecimientoDiagnostico(request.getPadecimientoDiagnostico());
        reclamacion.setInfoAdicional(request.getInfoAdicional());

        return reclamacion;
    }

}