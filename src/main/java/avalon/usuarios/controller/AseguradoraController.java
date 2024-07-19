package avalon.usuarios.controller;

import avalon.usuarios.model.pojo.Aseguradora;
import avalon.usuarios.model.pojo.Broker;
import avalon.usuarios.model.request.*;
import avalon.usuarios.model.response.CreateAseguradoraResponse;
import avalon.usuarios.model.response.PaginatedResponse;
import avalon.usuarios.service.AseguradoraService;
import avalon.usuarios.service.AseguradoraServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class AseguradoraController {

    private final AseguradoraService service;

    @PostMapping("/aseguradoras")
    public ResponseEntity<Aseguradora> createAseguradora(@RequestBody AseguradoraRequest request) {
        try {
            Aseguradora aseguradora = this.mapToAseguradora(request);
            aseguradora.setEstado("A");
            service.createAseguradora(aseguradora);
            return aseguradora.getId() != null ? ResponseEntity.status(HttpStatus.CREATED).body(aseguradora) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/aseguradoras")
    public ResponseEntity<PaginatedResponse<Aseguradora>> getAseguradoras(@RequestParam(required = false) String estado,
                                                                @RequestParam(required = false) String busqueda,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size,
                                                                @RequestParam(defaultValue = "createdDate") String sortField,
                                                                @RequestParam(defaultValue = "asc") String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("desc") ? Sort.by(sortField).descending() : Sort.by(sortField).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Aseguradora> aseguradoraPage = service.searchAseguradoras(estado, busqueda, pageable);

        List<Aseguradora> aseguradoras = aseguradoraPage.getContent();
        long totalRecords = aseguradoraPage.getTotalElements();

        PaginatedResponse<Aseguradora> response = new PaginatedResponse<>(aseguradoras, totalRecords);
        return ResponseEntity.ok(response);
    }


//    @GetMapping("/usuarios/{usuarioId}/aseguradoras")
//    public ResponseEntity<List<CreateAseguradoraResponse>> getAseguradorasByUsuario(@PathVariable Long usuarioId, @RequestParam(required = false) String estado) {
//        List<CreateAseguradoraResponse> aseguradoras = service.getAseguradoraByUsuarioAndEstado(usuarioId, estado);
//
//        if (!aseguradoras.isEmpty()) {
//            return ResponseEntity.ok(aseguradoras);
//        } else {
//            return ResponseEntity.ok(Collections.emptyList());
//        }
//    }

    @GetMapping("/aseguradoras/{aseguradoraId}")
    public ResponseEntity<Aseguradora> getAseguradora(@PathVariable Long aseguradoraId) {
        Aseguradora aseguradora = service.getAseguradora(aseguradoraId).orElseThrow(() -> new IllegalArgumentException("Aseguradora no encontrada"));

        if (aseguradora != null) {
            return ResponseEntity.ok(aseguradora);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/aseguradoras/{aseguradoraId}")
    public ResponseEntity<Aseguradora> updateAseguradora(@PathVariable Long aseguradoraId, @RequestBody AseguradoraRequest request) {
        Aseguradora aseguradora = service.getAseguradora(aseguradoraId).orElseThrow(() -> new IllegalArgumentException("Aseguradora no encontrada"));
        aseguradora.setNombre(request.getNombre());
        aseguradora.setCorreoElectronico(request.getCorreoElectronico());
        service.createAseguradora(aseguradora);

        return aseguradora != null ? ResponseEntity.ok(aseguradora) : ResponseEntity.badRequest().build();
    }

    @PatchMapping("/aseguradoras/{aseguradoraId}")
    public ResponseEntity<Aseguradora> partiallyUpdateAseguradora(@RequestBody PartiallyUpdateAseguradora request, @PathVariable Long aseguradoraId) {
        Aseguradora result = service.partiallyUpdateAseguradora(request, aseguradoraId);

        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/aseguradoras/{aseguradoraId}")
    public ResponseEntity<Void> deleteAseguradora(@PathVariable Long aseguradoraId) {
        service.deleteAseguradora(aseguradoraId);
        return ResponseEntity.noContent().build();
    }

    private Aseguradora mapToAseguradora(AseguradoraRequest request) {
        return Aseguradora.builder()
                .nombre(request.getNombre())
                .correoElectronico(request.getCorreoElectronico())
                .build();
    }

}