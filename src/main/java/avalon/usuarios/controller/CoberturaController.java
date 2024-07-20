package avalon.usuarios.controller;

import avalon.usuarios.model.pojo.Beneficio;
import avalon.usuarios.model.pojo.Cobertura;
import avalon.usuarios.model.pojo.Membresia;
import avalon.usuarios.model.pojo.Poliza;
import avalon.usuarios.model.request.BeneficioRequest;
import avalon.usuarios.model.request.CoberturaRequest;
import avalon.usuarios.model.response.PaginatedResponse;
import avalon.usuarios.service.BeneficioService;
import avalon.usuarios.service.CoberturaService;
import avalon.usuarios.service.MembresiaService;
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
public class CoberturaController {

    private final CoberturaService service;
    @Autowired
    private PolizaService polizaService;

    @PostMapping("/coberturas")
    public ResponseEntity<Cobertura> createCobertura(@RequestBody CoberturaRequest request) {
        try {
            Cobertura cobertura = this.mapToCobertura(request, new Cobertura());
            service.saveCobertura(cobertura);
            return cobertura.getId() != null ? ResponseEntity.status(HttpStatus.CREATED).body(cobertura) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/coberturas")
    public ResponseEntity<List<Cobertura>> getCoberturas() {
        List<Cobertura> coberturas = service.getCoberturas();

        if (!coberturas.isEmpty()) {
            return ResponseEntity.ok(coberturas);
        } else {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @GetMapping("/polizas/{polizaId}/coberturas")
    public ResponseEntity<PaginatedResponse<Cobertura>> getCoberturaByMembresia(@PathVariable Long polizaId,
                                                                   @RequestParam(required = false) String busqueda,
                                                                   @RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size,
                                                                   @RequestParam(defaultValue = "createdDate") String sortField,
                                                                   @RequestParam(defaultValue = "desc") String sortOrder) {
        Poliza poliza = polizaService.getPoliza(polizaId).orElseThrow(() -> new IllegalArgumentException("Poliza no encontrada"));
        Sort sort = sortOrder.equalsIgnoreCase("desc") ? Sort.by(sortField).descending() : Sort.by(sortField).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Cobertura> coberturaPage = service.searchCoberturasByPoliza(busqueda, pageable, poliza);

        List<Cobertura> coberturas = coberturaPage.getContent();
        long totalRecords = coberturaPage.getTotalElements();

        PaginatedResponse<Cobertura> response = new PaginatedResponse<>(coberturas, totalRecords);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/coberturas/{coberturaId}")
    public ResponseEntity<Cobertura> getCobertura(@PathVariable Long coberturaId) {
        Cobertura cobertura = service.getCobertura(coberturaId).orElseThrow(() -> new IllegalArgumentException("Cobertura no encontrada"));

        if (cobertura != null) {
            return ResponseEntity.ok(cobertura);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/coberturas/{coberturaId}")
    public ResponseEntity<Cobertura> updateCobertura(@PathVariable Long coberturaId, @RequestBody CoberturaRequest request) {
        Cobertura cobertura = service.getCobertura(coberturaId).orElseThrow(() -> new IllegalArgumentException("Cobertura no encontrada"));
        Cobertura coberturaMapped = this.mapToCobertura(request, cobertura);
        service.saveCobertura(coberturaMapped);

        return coberturaMapped != null ? ResponseEntity.ok(coberturaMapped) : ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/coberturas/{coberturaId}")
    public ResponseEntity<Void> deleteCobertura(@PathVariable Long coberturaId) {
        service.deleteCobertura(coberturaId);
        return ResponseEntity.noContent().build();
    }

    private Cobertura mapToCobertura(CoberturaRequest request, Cobertura cobertura) {
        Poliza poliza = this.polizaService.getPoliza(request.getPolizaId()).orElseThrow(() -> new IllegalArgumentException("Cobertura no encontrado"));

        cobertura.setNombre(request.getNombre());
        cobertura.setDescripcion(request.getDescripcion());
        cobertura.setPoliza(poliza);
        return cobertura;
    }


}