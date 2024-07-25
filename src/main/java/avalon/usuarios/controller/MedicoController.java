package avalon.usuarios.controller;

import avalon.usuarios.model.pojo.*;
import avalon.usuarios.model.request.MedicoRequest;
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

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MedicoController {

    private final MedicoService service;
    @Autowired
    private EspecialidadService especialidadService;
    @Autowired
    private PaisService paisService;
    @Autowired
    private EstadosService estadosService;

    @PostMapping("/medicos")
    public ResponseEntity<Medico> createMedico(@RequestBody MedicoRequest request) {
        try {
            Medico medico = this.mapToMedico(request, new Medico(), new Direccion());
            service.save(medico);
            return medico.getId() != null ? ResponseEntity.status(HttpStatus.CREATED).body(medico) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/medicos")
    public ResponseEntity<PaginatedResponse<Medico>> getMedicos(@RequestParam(required = false) String busqueda,
                                                                              @RequestParam(defaultValue = "0") int page,
                                                                              @RequestParam(defaultValue = "10") int size,
                                                                              @RequestParam(defaultValue = "createdDate") String sortField,
                                                                              @RequestParam(defaultValue = "desc") String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("desc") ? Sort.by(sortField).descending() : Sort.by(sortField).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Medico> medicoPage = service.searchMedicos(busqueda, pageable);

        List<Medico> medicos = medicoPage.getContent();
        long totalRecords = medicoPage.getTotalElements();

        PaginatedResponse<Medico> response = new PaginatedResponse<>(medicos, totalRecords);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/medicos/{medicoId}")
    public ResponseEntity<Medico> getMedico(@PathVariable Long medicoId) {
        Medico medico = service.findById(medicoId).orElseThrow(() -> new IllegalArgumentException("Medico no encontrada"));

        if (medico != null) {
            return ResponseEntity.ok(medico);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/medicos/{medicoId}")
    public ResponseEntity<Medico> updateMedico(@PathVariable Long medicoId, @RequestBody MedicoRequest request) {
        Medico medico = service.findById(medicoId).orElseThrow(() -> new IllegalArgumentException("Medico no encontrada"));
        Medico medicoMapped = this.mapToMedico(request, medico, medico.getDireccion());
        service.save(medicoMapped);

        return medicoMapped.getId() != null ? ResponseEntity.ok(medicoMapped) : ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/medicos/{medicoId}")
    public ResponseEntity<Void> deleteMedico(@PathVariable Long medicoId) {
        service.deleteById(medicoId);
        return ResponseEntity.noContent().build();
    }

    private Medico mapToMedico(MedicoRequest request, Medico medico, Direccion direccion) {
        Especialidad especialidad = especialidadService.getEspecialidad(request.getEspecialidadId()).
                orElseThrow(() -> new IllegalArgumentException("Especialidad no encontrada"));
        Pais pais = paisService.findById(request.getDireccion().getPaisId()).orElseThrow(() -> new IllegalArgumentException("País no encontrado"));
        Estado estado = estadosService.findById(request.getDireccion().getEstadoId()).orElseThrow(() -> new IllegalArgumentException("Estado no encontrado"));

        medico.setNombres(request.getNombres());
        medico.setNombresDos(request.getNombresDos());
        medico.setApellidos(request.getApellidos());
        medico.setApellidosDos(request.getApellidosDos());
        medico.setEspecialidad(especialidad);
        medico.setCorreoElectronico(request.getCorreoElectronico());
        medico.setNumeroTelefono(request.getNumeroTelefono());
        medico.setUrlImagen(request.getUrlImagen());
        medico.setEstado(request.getEstado());

        direccion.setDireccionUno(request.getDireccion().getDireccionUno());
        direccion.setDireccionDos(request.getDireccion().getDireccionDos());
        direccion.setCodigoPostal(request.getDireccion().getCodigoPostal());
        direccion.setPais(pais);
        direccion.setState(estado);
        direccion.setCiudad(request.getDireccion().getCiudad());
        medico.setDireccion(direccion);

        return medico;
    }


}