package avalon.usuarios.controller;

import avalon.usuarios.model.pojo.Aseguradora;
import avalon.usuarios.model.pojo.Asesor;
import avalon.usuarios.model.pojo.Beneficio;
import avalon.usuarios.model.pojo.Membresia;
import avalon.usuarios.model.request.AseguradoraRequest;
import avalon.usuarios.model.request.BeneficioRequest;
import avalon.usuarios.model.request.PartiallyUpdateAseguradora;
import avalon.usuarios.model.response.PaginatedResponse;
import avalon.usuarios.service.BeneficioService;
import avalon.usuarios.service.MembresiaService;
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
public class BeneficioController {

    private final BeneficioService service;
    @Autowired
    private MembresiaService membresiaService;

    @PostMapping("/beneficios")
    public ResponseEntity<Beneficio> createBeneficio(@RequestBody BeneficioRequest request) {
        try {
            Beneficio beneficio = this.mapToBeneficio(request, new Beneficio());
            service.saveBeneficio(beneficio);
            return beneficio.getId() != null ? ResponseEntity.status(HttpStatus.CREATED).body(beneficio) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/beneficios")
    public ResponseEntity<List<Beneficio>> getBeneficios() {
        List<Beneficio> beneficios = service.getBeneficios();

        if (!beneficios.isEmpty()) {
            return ResponseEntity.ok(beneficios);
        } else {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

//    @GetMapping("/membresias/{membresiaId}/beneficios")
//    public ResponseEntity<List<Beneficio>> getBeneficioByMembresia(@PathVariable Long membresiaId) {
//        Membresia membresia = membresiaService.getMembresia(membresiaId).orElseThrow(() -> new IllegalArgumentException("Membresia no encontrada"));
//        List<Beneficio> beneficios = service.getBeneficiosByMembresia(membresia);
//
//        if (!beneficios.isEmpty()) {
//            return ResponseEntity.ok(beneficios);
//        } else {
//            return ResponseEntity.ok(Collections.emptyList());
//        }
//    }

    @GetMapping("/membresias/{membresiaId}/beneficios")
    public ResponseEntity<PaginatedResponse<Beneficio>> getBeneficioByMembresia(@PathVariable Long membresiaId,
                                                                   @RequestParam(required = false) String busqueda,
                                                                   @RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size,
                                                                   @RequestParam(defaultValue = "createdDate") String sortField,
                                                                   @RequestParam(defaultValue = "desc") String sortOrder) {
        Membresia membresia = membresiaService.getMembresia(membresiaId).orElseThrow(() -> new IllegalArgumentException("Membresia no encontrada"));

        Sort sort = sortOrder.equalsIgnoreCase("desc") ? Sort.by(sortField).descending() : Sort.by(sortField).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Beneficio> beneficioPage = service.searchBeneficiosByMembresia(busqueda, pageable, membresia);

        List<Beneficio> beneficios = beneficioPage.getContent();
        long totalRecords = beneficioPage.getTotalElements();

        PaginatedResponse<Beneficio> response = new PaginatedResponse<>(beneficios, totalRecords);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/beneficios/{beneficioId}")
    public ResponseEntity<Beneficio> getBeneficio(@PathVariable Long beneficioId) {
        Beneficio beneficio = service.getBeneficio(beneficioId).orElseThrow(() -> new IllegalArgumentException("Beneficio no encontrada"));

        if (beneficio != null) {
            return ResponseEntity.ok(beneficio);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/beneficios/{beneficioId}")
    public ResponseEntity<Beneficio> updateBeneficio(@PathVariable Long beneficioId, @RequestBody BeneficioRequest request) {
        Beneficio beneficio = service.getBeneficio(beneficioId).orElseThrow(() -> new IllegalArgumentException("Beneficio no encontrada"));
        Beneficio beneficioMapped = this.mapToBeneficio(request, beneficio);
        service.saveBeneficio(beneficioMapped);

        return beneficioMapped != null ? ResponseEntity.ok(beneficioMapped) : ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/beneficios/{beneficioId}")
    public ResponseEntity<Void> deleteBeneficio(@PathVariable Long beneficioId) {
        service.deleteBeneficio(beneficioId);
        return ResponseEntity.noContent().build();
    }

    private Beneficio mapToBeneficio(BeneficioRequest request, Beneficio beneficio) {
        Membresia membresia = this.membresiaService.getMembresia(request.getMembresiaId()).orElseThrow(() -> new IllegalArgumentException("Membresía no encontrado"));

        beneficio.setNombre(request.getNombre());
        beneficio.setDescripcion(request.getDescripcion());
        beneficio.setMembresia(membresia);
        return beneficio;
    }


}