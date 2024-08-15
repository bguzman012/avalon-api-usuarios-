package avalon.usuarios.controller;

import avalon.usuarios.model.pojo.*;
import avalon.usuarios.model.request.CasoRequest;
import avalon.usuarios.model.response.PaginatedResponse;
import avalon.usuarios.service.CasoService;
import avalon.usuarios.service.ClientesPolizaService;
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
public class CasoController {

    private final CasoService service;
    @Autowired
    private ClientesPolizaService clientesPolizaService;

    @PostMapping("/casos")
    public ResponseEntity<Caso> createCaso(@RequestBody CasoRequest request) {
        try {
            Caso caso = this.mapToCaso(request, new Caso());
            service.saveCaso(caso);
            return caso.getId() != null ? ResponseEntity.status(HttpStatus.CREATED).body(caso) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/casos")
    public ResponseEntity<PaginatedResponse<Caso>> getCasos(@RequestParam(required = false) String busqueda,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size,
                                                            @RequestParam(defaultValue = "createdDate") String sortField,
                                                            @RequestParam(defaultValue = "asc") String sortOrder,
                                                            @RequestParam(required = false) String clientePolizaId) {
        Long clientePoliza = null;

        Sort sort = sortOrder.equalsIgnoreCase("desc") ? Sort.by(sortField).descending() : Sort.by(sortField).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        if (clientePolizaId != null && !clientePolizaId.isBlank())
            clientePoliza = Long.valueOf(clientePolizaId);

        Page<Caso> casoPage = service.searchCasos(busqueda, pageable, clientePoliza);

        List<Caso> casos = casoPage.getContent();
        long totalRecords = casoPage.getTotalElements();

        PaginatedResponse<Caso> response = new PaginatedResponse<>(casos, totalRecords);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/casos/{casoId}")
    public ResponseEntity<Caso> getCaso(@PathVariable Long casoId) {
        Caso caso = service.getCaso(casoId).orElseThrow(() -> new IllegalArgumentException("Caso no encontrado"));

        if (caso != null) {
            return ResponseEntity.ok(caso);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/casos/{casoId}")
    public ResponseEntity<Caso> updateCaso(@PathVariable Long casoId, @RequestBody CasoRequest request) {
        Caso caso = service.getCaso(casoId).orElseThrow(() -> new IllegalArgumentException("Caso no encontrado"));
        Caso casoMapped = this.mapToCaso(request, caso);

        service.saveCaso(casoMapped);

        return casoMapped != null ? ResponseEntity.ok(casoMapped) : ResponseEntity.badRequest().build();
    }


    @DeleteMapping("/casos/{casoId}")
    public ResponseEntity<Void> deleteCaso(@PathVariable Long casoId) {
        service.deleteCaso(casoId);
        return ResponseEntity.noContent().build();
    }

    private Caso mapToCaso(CasoRequest request, Caso caso) {
        ClientePoliza clientePoliza = clientesPolizaService.getClientePoliza(request.getClientePolizaId()).orElseThrow(() -> new IllegalArgumentException("Cliente Poliza no encontrado"));

        caso.setClientePoliza(clientePoliza);
        caso.setObservaciones(request.getObservaciones());

        return caso;
    }
}