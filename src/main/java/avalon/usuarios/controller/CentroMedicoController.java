package avalon.usuarios.controller;

import avalon.usuarios.model.pojo.*;
import avalon.usuarios.model.request.CentroMedicoRequest;
import avalon.usuarios.model.response.PaginatedResponse;
import avalon.usuarios.service.CentroMedicoService;
import avalon.usuarios.service.EstadosService;
import avalon.usuarios.service.PaisService;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CentroMedicoController {

    private final CentroMedicoService service;
    @Autowired
    private PaisService paisService;
    @Autowired
    private EstadosService estadosService;

    @PostMapping("/centrosMedicos")
    public ResponseEntity<CentroMedico> createCentroMedico(@RequestBody CentroMedicoRequest request) {
        try {
            CentroMedico centroMedico = this.mapToCentroMedico(request, new CentroMedico(), new Direccion());
            service.saveCentroMedico(centroMedico);
            return centroMedico.getId() != null ? ResponseEntity.status(HttpStatus.CREATED).body(centroMedico) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/centrosMedicos")
    public ResponseEntity<PaginatedResponse<CentroMedico>> getCentrosMedicos(@RequestParam(required = false) String busqueda,
                                                                             @RequestParam(defaultValue = "0") int page,
                                                                             @RequestParam(defaultValue = "10") int size,
                                                                             @RequestParam(defaultValue = "createdDate") String sortField,
                                                                             @RequestParam(defaultValue = "asc") String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("desc") ? Sort.by(sortField).descending() : Sort.by(sortField).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<CentroMedico> centroMedicoPage = service.searchCentrosMedicos(busqueda, pageable);

        List<CentroMedico> centrosMedicos = centroMedicoPage.getContent();
        long totalRecords = centroMedicoPage.getTotalElements();

        PaginatedResponse<CentroMedico> response = new PaginatedResponse<>(centrosMedicos, totalRecords);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/centrosMedicos/{centroMedicoId}")
    public ResponseEntity<CentroMedico> getCentroMedico(@PathVariable Long centroMedicoId) {
        CentroMedico centroMedico = service.getCentroMedico(centroMedicoId).orElseThrow(() -> new IllegalArgumentException("CentroMedico no encontrada"));

        if (centroMedico != null) {
            return ResponseEntity.ok(centroMedico);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/centrosMedicos/{centroMedicoId}")
    public ResponseEntity<CentroMedico> updateCentroMedico(@PathVariable Long centroMedicoId, @RequestBody CentroMedicoRequest request) {
        CentroMedico centroMedico = service.getCentroMedico(centroMedicoId).orElseThrow(() -> new IllegalArgumentException("CentroMedico no encontrada"));
        CentroMedico centroMedicoMapped = this.mapToCentroMedico(request, centroMedico, centroMedico.getDireccion());

        service.saveCentroMedico(centroMedicoMapped);

        return centroMedico != null ? ResponseEntity.ok(centroMedico) : ResponseEntity.badRequest().build();
    }


    @DeleteMapping("/centrosMedicos/{centroMedicoId}")
    public ResponseEntity<Void> deleteCentroMedico(@PathVariable Long centroMedicoId) {
        service.deleteCentroMedico(centroMedicoId);
        return ResponseEntity.noContent().build();
    }

    private CentroMedico mapToCentroMedico(CentroMedicoRequest request, CentroMedico centroMedico, Direccion direccion) {
        Pais pais = paisService.findById(request.getDireccion().getPaisId()).orElseThrow(() -> new IllegalArgumentException("País no encontrado"));
        Estado estado = estadosService.findById(request.getDireccion().getEstadoId()).orElseThrow(() -> new IllegalArgumentException("Estado no encontrado"));

        centroMedico.setNombre(request.getNombre());
        centroMedico.setDescripcion(request.getDescripcion());
        centroMedico.setCorreoElectronico(request.getCorreoElectronico());

        direccion.setDireccionUno(request.getDireccion().getDireccionUno());
        direccion.setDireccionDos(request.getDireccion().getDireccionDos());
        direccion.setCodigoPostal(request.getDireccion().getCodigoPostal());
        direccion.setPais(pais);
        direccion.setState(estado);
        direccion.setCiudad(request.getDireccion().getCiudad());
        centroMedico.setDireccion(direccion);

        return centroMedico;
    }
}