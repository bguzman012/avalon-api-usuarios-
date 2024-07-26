package avalon.usuarios.controller;

import avalon.usuarios.model.pojo.*;
import avalon.usuarios.model.request.MedicoCentroMedicoAseguradoraRequest;
import avalon.usuarios.model.response.PaginatedResponse;
import avalon.usuarios.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MedicoCentroMedicoAseguradoraController {

    private final MedicoCentroMedicoAseguradoraService service;
    @Autowired
    private MedicoService medicoService;
    @Autowired
    private CentroMedicoService centroMedicoService;
    @Autowired
    private AseguradoraService aseguradoraService;

    @PostMapping("/medicoCentroMedicoAseguradoras")
    public ResponseEntity<?> createMedicoCentroMedicoAseguradora(@RequestBody MedicoCentroMedicoAseguradoraRequest request) {
        try {
            MedicoCentroMedicoAseguradora medicoCentroMedicoAseguradora = this.mapToMedicoCentroMedicoAseguradora(request, new MedicoCentroMedicoAseguradora());
            MedicoCentroMedicoAseguradora result = service.saveMedicoCentroMedicoAseguradora(medicoCentroMedicoAseguradora);
            return result.getId() != null ? ResponseEntity.status(HttpStatus.CREATED).body(result) : ResponseEntity.badRequest().build();
        } catch (DataIntegrityViolationException e) {
            // Manejo de la excepción para duplicados
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Ya existe una relación con los mismos valores de aseguradora, médico y centro médico.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocurrió un error inesperado. Por favor, inténtelo de nuevo más tarde.");
        }
    }

    @GetMapping("aseguradoras/{aseguradoraId}/centrosMedicos/{centroMedicoId}/medicoCentroMedicoAseguradoras")
    public ResponseEntity<PaginatedResponse<MedicoCentroMedicoAseguradora>> getMedicoCentroMedicoAseguradorasByAseguradoraAndCentroMedico(@PathVariable Long aseguradoraId,
                                                                                                                                          @PathVariable Long centroMedicoId,
                                                                                                                                          @RequestParam(required = false) String busqueda,
                                                                                                                                          @RequestParam(defaultValue = "0") int page,
                                                                                                                                          @RequestParam(defaultValue = "10") int size,
                                                                                                                                          @RequestParam(defaultValue = "createdDate") String sortField,
                                                                                                                                          @RequestParam(defaultValue = "desc") String sortOrder) {
        Aseguradora aseguradora = this.aseguradoraService.getAseguradora(aseguradoraId).orElseThrow(() -> new IllegalArgumentException("Aseguradora no encontrada"));
        CentroMedico centroMedico = this.centroMedicoService.getCentroMedico(centroMedicoId).orElseThrow(() -> new IllegalArgumentException("Centro médico no encontrada"));

        Sort sort = sortOrder.equalsIgnoreCase("desc") ? Sort.by(sortField).descending() : Sort.by(sortField).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<MedicoCentroMedicoAseguradora> medicoCentroMedicoAseguradoraPage = service.searchMedicoCentroMedicoAseguradoras(busqueda, pageable, null, aseguradora, centroMedico);

        List<MedicoCentroMedicoAseguradora> medicoCentroMedicoAseguradoras = medicoCentroMedicoAseguradoraPage.getContent();
        long totalRecords = medicoCentroMedicoAseguradoraPage.getTotalElements();

        PaginatedResponse<MedicoCentroMedicoAseguradora> response = new PaginatedResponse<>(medicoCentroMedicoAseguradoras, totalRecords);
        return ResponseEntity.ok(response);
    }

    @GetMapping("medicos/{medicoId}/medicoCentroMedicoAseguradoras")
    public ResponseEntity<PaginatedResponse<MedicoCentroMedicoAseguradora>> getMedicoCentroMedicoAseguradorasByMedico(@PathVariable Long medicoId,
                                                                                                                      @RequestParam(required = false) String busqueda,
                                                                                                                      @RequestParam(defaultValue = "0") int page,
                                                                                                                      @RequestParam(defaultValue = "10") int size,
                                                                                                                      @RequestParam(defaultValue = "createdDate") String sortField,
                                                                                                                      @RequestParam(defaultValue = "desc") String sortOrder) {
        Medico medico = this.medicoService.findById(medicoId).orElseThrow(() -> new IllegalArgumentException("Medico no encontrado"));
        Sort sort = sortOrder.equalsIgnoreCase("desc") ? Sort.by(sortField).descending() : Sort.by(sortField).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<MedicoCentroMedicoAseguradora> medicoCentroMedicoAseguradoraPage = service.searchMedicoCentroMedicoAseguradoras(busqueda, pageable, medico, null, null);

        List<MedicoCentroMedicoAseguradora> medicoCentroMedicoAseguradoras = medicoCentroMedicoAseguradoraPage.getContent();
        long totalRecords = medicoCentroMedicoAseguradoraPage.getTotalElements();

        PaginatedResponse<MedicoCentroMedicoAseguradora> response = new PaginatedResponse<>(medicoCentroMedicoAseguradoras, totalRecords);
        return ResponseEntity.ok(response);
    }

    @GetMapping("aseguradoras/{aseguradoraId}/medicoCentroMedicoAseguradoras")
    public ResponseEntity<PaginatedResponse<MedicoCentroMedicoAseguradora>> getMedicoCentroMedicoAseguradorasByAseguradora(@PathVariable Long aseguradoraId,
                                                                                                                           @RequestParam(required = false) String busqueda,
                                                                                                                           @RequestParam(defaultValue = "0") int page,
                                                                                                                           @RequestParam(defaultValue = "10") int size,
                                                                                                                           @RequestParam(defaultValue = "createdDate") String sortField,
                                                                                                                           @RequestParam(defaultValue = "desc") String sortOrder) {
        Aseguradora aseguradora = this.aseguradoraService.getAseguradora(aseguradoraId).orElseThrow(() -> new IllegalArgumentException("Aseguradora no encontrada"));
        Sort sort = sortOrder.equalsIgnoreCase("desc") ? Sort.by(sortField).descending() : Sort.by(sortField).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<MedicoCentroMedicoAseguradora> medicoCentroMedicoAseguradoraPage = service.searchMedicoCentroMedicoAseguradoras(busqueda, pageable, null, aseguradora, null);

        List<MedicoCentroMedicoAseguradora> medicoCentroMedicoAseguradoras = medicoCentroMedicoAseguradoraPage.getContent();
        long totalRecords = medicoCentroMedicoAseguradoraPage.getTotalElements();

        PaginatedResponse<MedicoCentroMedicoAseguradora> response = new PaginatedResponse<>(medicoCentroMedicoAseguradoras, totalRecords);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/medicoCentroMedicoAseguradoras/{medicoCentroMedicoAseguradoraId}")
    public ResponseEntity<MedicoCentroMedicoAseguradora> getUsuarioMembresia(@PathVariable Long medicoCentroMedicoAseguradoraId) {
        MedicoCentroMedicoAseguradora medicoCentroMedicoAseguradora = service.getMedicoCentroMedicoAseguradora(medicoCentroMedicoAseguradoraId).orElseThrow(() -> new IllegalArgumentException("Cliente Membresía no encontrado"));
        ;

        if (medicoCentroMedicoAseguradora != null) {
            return ResponseEntity.ok(medicoCentroMedicoAseguradora);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/medicoCentroMedicoAseguradoras/{medicoCentroMedicoAseguradoraId}")
    public ResponseEntity<?> updateUsuarioMembresia(@PathVariable Long medicoCentroMedicoAseguradoraId, @RequestBody MedicoCentroMedicoAseguradoraRequest request) {
        try {
            MedicoCentroMedicoAseguradora medicoCentroMedicoAseguradora = service.getMedicoCentroMedicoAseguradora(medicoCentroMedicoAseguradoraId).orElseThrow(() -> new IllegalArgumentException("Cliente Membresía no encontrado"));
            MedicoCentroMedicoAseguradora medicoCentroMedicoAseguradoraMapped = this.mapToMedicoCentroMedicoAseguradora(request, medicoCentroMedicoAseguradora);
            this.service.saveMedicoCentroMedicoAseguradora(medicoCentroMedicoAseguradoraMapped);
            return medicoCentroMedicoAseguradoraMapped != null ? ResponseEntity.ok(medicoCentroMedicoAseguradoraMapped) : ResponseEntity.badRequest().build();
        } catch (DataIntegrityViolationException e) {
            // Manejo de la excepción para duplicados
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Ya existe una relación con los mismos valores de aseguradora, médico y centro médico.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocurrió un error inesperado. Por favor, inténtelo de nuevo más tarde.");
        }
    }

    @DeleteMapping("/medicoCentroMedicoAseguradoras/{medicoCentroMedicoAseguradoraId}")
    public ResponseEntity<Void> deleteUsuarioMembresia(@PathVariable Long medicoCentroMedicoAseguradoraId) {
        service.deleteMedicoCentroMedicoAseguradora(medicoCentroMedicoAseguradoraId);
        return ResponseEntity.noContent().build();
    }

    private MedicoCentroMedicoAseguradora mapToMedicoCentroMedicoAseguradora(MedicoCentroMedicoAseguradoraRequest request, MedicoCentroMedicoAseguradora medicoCentroMedicoAseguradoraReference) {
        Medico medico = this.medicoService.findById(request.getMedicoId()).orElseThrow(() -> new IllegalArgumentException("Médico no encontrado"));
        CentroMedico centroMedico = this.centroMedicoService.getCentroMedico(request.getCentroMedicoId()).orElseThrow(() -> new IllegalArgumentException("Centro Médico no encontrado"));
        Aseguradora aseguradora = this.aseguradoraService.getAseguradora(request.getAseguradoraId()).orElseThrow(() -> new IllegalArgumentException("Aseguradora no encontrada"));

        medicoCentroMedicoAseguradoraReference.setMedico(medico);
        medicoCentroMedicoAseguradoraReference.setAseguradora(aseguradora);
        medicoCentroMedicoAseguradoraReference.setCentroMedico(centroMedico);
        return medicoCentroMedicoAseguradoraReference;
    }


}
