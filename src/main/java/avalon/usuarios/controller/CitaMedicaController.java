package avalon.usuarios.controller;

import avalon.usuarios.model.pojo.*;
import avalon.usuarios.model.request.CentroMedicoRequest;
import avalon.usuarios.model.request.CitaMedicaRequest;
import avalon.usuarios.model.request.PartiallyUpdateCitaMedicaRequest;
import avalon.usuarios.model.request.ReclamacionRequest;
import avalon.usuarios.model.response.PaginatedResponse;
import avalon.usuarios.service.ClientesPolizaService;
import avalon.usuarios.service.CitaMedicaService;
import avalon.usuarios.service.ImagenService;
import avalon.usuarios.service.MedicoCentroMedicoAseguradoraService;
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
public class CitaMedicaController {

    private final CitaMedicaService service;
    @Autowired
    private ClientesPolizaService clientesPolizaService;
    @Autowired
    private MedicoCentroMedicoAseguradoraService medicoCentroMedicoAseguradoraService;
    @Autowired
    private ImagenService imagenService;
    private String TOPICO = "IMAGEN_CITA_MEDICA";

    @PostMapping("/citasMedicas")
    public ResponseEntity<CitaMedica> createCitaMedica(@RequestPart("citaMedica") CitaMedicaRequest request,
                                                       @RequestPart("fotoCitaMedica") MultipartFile fotoCitaMedica) {
        try {
            request.setEstado("N");
            CitaMedica citaMedica = this.mapToCitaMedica(request, new CitaMedica());
            if (!fotoCitaMedica.isEmpty()) {
                Imagen imagen = new Imagen(fotoCitaMedica.getBytes(), this.TOPICO, request.getNombreDocumento());
                this.imagenService.saveImagen(imagen);
                citaMedica.setImagenId(imagen.getId());
            }

            service.saveCitaMedica(citaMedica);
            return citaMedica.getId() != null ? ResponseEntity.status(HttpStatus.CREATED).body(citaMedica) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/citasMedicas")
    public ResponseEntity<PaginatedResponse<CitaMedica>> getCitasMedicas(@RequestParam(required = false) String estado,
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

        Page<CitaMedica> citaMedicaPage = service.searchCitasMedicas(busqueda, estado, pageable, clientePoliza);

        List<CitaMedica> citasMedicas = citaMedicaPage.getContent();
        long totalRecords = citaMedicaPage.getTotalElements();

        PaginatedResponse<CitaMedica> response = new PaginatedResponse<>(citasMedicas, totalRecords);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/citasMedicas/{citaMedicaId}")
    public ResponseEntity<CitaMedica> getCitaMedicaById(@PathVariable Long citaMedicaId) {
        try {
            return service.getCitaMedica(citaMedicaId).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/citasMedicas/{citaMedicaId}")
    public ResponseEntity<CitaMedica> partiallyUpdateCitaMedica(@RequestBody PartiallyUpdateCitaMedicaRequest request, @PathVariable Long citaMedicaId) {
        try {
            CitaMedica citaMedica = service.partiallyUpdateCitaMedica(request, citaMedicaId);
            return citaMedica == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(citaMedica);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/citasMedicas/{citaMedicaId}")
    public ResponseEntity<CitaMedica> updateCitaMedica(@PathVariable Long citaMedicaId,
                                                       @RequestPart("citaMedica") CitaMedicaRequest request,
                                                       @RequestPart(value="fotoCitaMedica", required = false) MultipartFile fotoCitaMedica) {
        try {
            CitaMedica citaMedica = service.getCitaMedica(citaMedicaId).orElseThrow(() -> new IllegalArgumentException("Cita Médica no encontrada"));
            CitaMedica citaMedicaMapped = this.mapToCitaMedica(request, citaMedica);

            if (citaMedicaMapped.getImagenId() != null && fotoCitaMedica != null) {
                this.imagenService.deleteImagen(citaMedica.getImagenId());
                citaMedicaMapped.setImagenId(null);
            }

            if (fotoCitaMedica != null && !fotoCitaMedica.isEmpty()) {
                Imagen imagen = new Imagen(fotoCitaMedica.getBytes(), this.TOPICO, request.getNombreDocumento());
                this.imagenService.saveImagen(imagen);
                citaMedicaMapped.setImagenId(imagen.getId());
            }

            service.saveCitaMedica(citaMedicaMapped);

            return citaMedicaMapped.getId() != null ? ResponseEntity.ok(citaMedicaMapped) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/citasMedicas/{citaMedicaId}")
    public ResponseEntity<Void> deleteCitaMedica(@PathVariable Long citaMedicaId) {
        try {
            service.deleteCitaMedica(citaMedicaId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private CitaMedica mapToCitaMedica(CitaMedicaRequest request, CitaMedica citaMedica) {
        ClientePoliza clientePoliza = clientesPolizaService.getClientePoliza(request.getClientePolizaId()).orElseThrow(() -> new IllegalArgumentException("Cliente Poliza no encontrada"));
        MedicoCentroMedicoAseguradora medicoCentroMedicoAseguradora = medicoCentroMedicoAseguradoraService.getMedicoCentroMedicoAseguradora(
                request.getMedicoCentroMedicoAseguradoraId()).orElseThrow(() -> new IllegalArgumentException("Centro Médico no encontrado"));

        citaMedica.setEstado(request.getEstado());
        citaMedica.setClientePoliza(clientePoliza);
        citaMedica.setMedicoCentroMedicoAseguradora(medicoCentroMedicoAseguradora);
        citaMedica.setFechaTentativa(request.getFechaTentativa());
        citaMedica.setCiudadPreferencia(request.getCiudadPreferencia());
        citaMedica.setPadecimiento(request.getPadecimiento());
        citaMedica.setInformacionAdicional(request.getInformacionAdicional());
        citaMedica.setRequisitosAdicionales(request.getRequisitosAdicionales());
        citaMedica.setOtrosRequisitos(request.getOtrosRequisitos());
        return citaMedica;
    }
}
