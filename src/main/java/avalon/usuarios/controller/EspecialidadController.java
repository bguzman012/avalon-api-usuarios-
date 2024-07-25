package avalon.usuarios.controller;

import avalon.usuarios.model.pojo.Cobertura;
import avalon.usuarios.model.pojo.Especialidad;
import avalon.usuarios.model.pojo.Poliza;
import avalon.usuarios.model.request.CoberturaRequest;
import avalon.usuarios.model.response.PaginatedResponse;
import avalon.usuarios.service.CoberturaService;
import avalon.usuarios.service.EspecialidadService;
import avalon.usuarios.service.PolizaService;
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
public class EspecialidadController {

    private final EspecialidadService service;

    @GetMapping("/especialidades")
    public ResponseEntity<PaginatedResponse<Especialidad>> getEspecialidades(@RequestParam(required = false) String busqueda,
                                                                             @RequestParam(defaultValue = "0") int page,
                                                                             @RequestParam(defaultValue = "10") int size,
                                                                             @RequestParam(defaultValue = "createdDate") String sortField,
                                                                             @RequestParam(defaultValue = "desc") String sortOrder) {

        Sort sort = sortOrder.equalsIgnoreCase("desc") ? Sort.by(sortField).descending() : Sort.by(sortField).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Especialidad> especialidadPage = service.searchEspecialidades(busqueda, pageable);

        List<Especialidad> especialidades = especialidadPage.getContent();
        long totalRecords = especialidadPage.getTotalElements();

        PaginatedResponse<Especialidad> response = new PaginatedResponse<>(especialidades, totalRecords);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/especialidades/{especialidadId}")
    public ResponseEntity<Especialidad> getEspecialidad(@PathVariable Long especialidadId) {
        Especialidad especialidad = service.getEspecialidad(especialidadId).orElseThrow(() -> new IllegalArgumentException("Especialidad no encontrada"));

        if (especialidad != null) {
            return ResponseEntity.ok(especialidad);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}