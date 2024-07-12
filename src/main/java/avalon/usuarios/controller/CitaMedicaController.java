package avalon.usuarios.controller;

import avalon.usuarios.model.pojo.ClientePoliza;
import avalon.usuarios.model.pojo.Imagen;
import avalon.usuarios.model.pojo.CitaMedica;
import avalon.usuarios.model.pojo.Reclamacion;
import avalon.usuarios.model.request.CitaMedicaRequest;
import avalon.usuarios.model.request.PartiallyUpdateCitaMedicaRequest;
import avalon.usuarios.model.request.ReclamacionRequest;
import avalon.usuarios.service.ClientesPolizaService;
import avalon.usuarios.service.CitaMedicaService;
import avalon.usuarios.service.ImagenService;
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
public class CitaMedicaController {

    private final CitaMedicaService service;
    @Autowired
    private ClientesPolizaService clientesPolizaService;
    @Autowired
    private ImagenService imagenService;
    private String TOPICO = "IMAGEN_CITA_MEDICA";

    @PostMapping("/citasMedicas")
    public ResponseEntity<CitaMedica> createCitaMedica(@RequestPart("citaMedica") CitaMedicaRequest request,
                                                       @RequestPart("fotoCitaMedica") MultipartFile fotoCitaMedica) {
        try {
            ClientePoliza clientePoliza = clientesPolizaService.getClientePoliza(request.getClientePolizaId()).orElseThrow(() -> new IllegalArgumentException("Cliente Poliza no encontrada"));
            CitaMedica citaMedica = this.mapToCitaMedica(request, clientePoliza);
            if (!fotoCitaMedica.isEmpty()) {
                Imagen imagen = new Imagen(fotoCitaMedica.getBytes(), this.TOPICO, request.getNombreDocumento());
                this.imagenService.saveImagen(imagen);
                citaMedica.setImagenId(imagen.getId());
            }

            citaMedica.setEstado("N");
            service.saveCitaMedica(citaMedica);
            return citaMedica.getId() != null ? ResponseEntity.status(HttpStatus.CREATED).body(citaMedica) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/citasMedicas")
    public ResponseEntity<List<CitaMedica>> getCitasMedicas(@RequestParam(required = false) String estado, @RequestParam(required = false) String clientePolizaId) {
        try {
            List<CitaMedica> citasMedicas = new ArrayList<>();

            if (!clientePolizaId.isBlank()) {
                ClientePoliza clientePoliza = clientesPolizaService.getClientePoliza(Long.valueOf(clientePolizaId)).orElseThrow(() -> new IllegalArgumentException("Cliente Poliza no encontrada"));
                citasMedicas = service.getCitasMedicasByClientePoliza(clientePoliza);
            } else if (estado.isBlank()) {
                citasMedicas = service.getCitasMedicas();
            }

            if (!citasMedicas.isEmpty()) {
                return ResponseEntity.ok(citasMedicas);
            } else {
                return ResponseEntity.ok(Collections.emptyList());
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
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
                                                         @RequestPart("reclamacion") CitaMedicaRequest request,
                                                         @RequestPart("fotoReclamo") MultipartFile fotoReclamo) {
        try {
            ClientePoliza clientePoliza = clientesPolizaService.getClientePoliza(request.getClientePolizaId()).orElseThrow(() -> new IllegalArgumentException("Cliente Poliza no encontrada"));
            CitaMedica citaMedica = service.getCitaMedica(citaMedicaId).orElseThrow(() -> new IllegalArgumentException("CitaMedica no encontrada"));
            citaMedica.setRazon(request.getRazon());

            if (citaMedica.getImagenId() != null)
                this.imagenService.deleteImagen(citaMedica.getImagenId());

            citaMedica.setImagenId(null);

            if (!fotoReclamo.isEmpty()) {
                Imagen imagen = new Imagen(fotoReclamo.getBytes(), this.TOPICO, request.getNombreDocumento());
                this.imagenService.saveImagen(imagen);
                citaMedica.setImagenId(imagen.getId());
            }

            citaMedica.setEstado(request.getEstado());
            citaMedica.setClientePoliza(clientePoliza);
            service.saveCitaMedica(citaMedica);

            return citaMedica.getId() != null ? ResponseEntity.ok(citaMedica) : ResponseEntity.badRequest().build();
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

    private CitaMedica mapToCitaMedica(CitaMedicaRequest request, ClientePoliza clientePoliza) {
        return CitaMedica.builder()
                .razon(request.getRazon())
                .estado(request.getEstado())
                .clientePoliza(clientePoliza)
                .build();
    }
}
