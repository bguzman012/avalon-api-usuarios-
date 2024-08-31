package avalon.usuarios.controller;

import avalon.usuarios.model.pojo.Empresa;
import avalon.usuarios.model.pojo.Direccion;
import avalon.usuarios.model.pojo.Estado;
import avalon.usuarios.model.pojo.Pais;
import avalon.usuarios.model.request.EmpresaRequest;
import avalon.usuarios.model.response.PaginatedResponse;
import avalon.usuarios.service.EmpresaService;
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
public class EmpresaController {

    private final EmpresaService service;

    @PostMapping("/empresas")
    public ResponseEntity<Empresa> createEmpresa(@RequestBody EmpresaRequest request) {
        try {
            Empresa empresa = this.mapToEmpresa(request, new Empresa());
            service.saveEmpresa(empresa);
            return empresa.getId() != null ? ResponseEntity.status(HttpStatus.CREATED).body(empresa) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/empresas")
    public ResponseEntity<PaginatedResponse<Empresa>> getEmpresas(@RequestParam(required = false) String busqueda,
                                                                             @RequestParam(defaultValue = "0") int page,
                                                                             @RequestParam(defaultValue = "10") int size,
                                                                             @RequestParam(defaultValue = "createdDate") String sortField,
                                                                             @RequestParam(defaultValue = "asc") String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("desc") ? Sort.by(sortField).descending() : Sort.by(sortField).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Empresa> empresaPage = service.searchEmpresas(busqueda, pageable);

        List<Empresa> empresas = empresaPage.getContent();
        long totalRecords = empresaPage.getTotalElements();

        PaginatedResponse<Empresa> response = new PaginatedResponse<>(empresas, totalRecords);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/empresas/{empresaId}")
    public ResponseEntity<Empresa> getEmpresa(@PathVariable Long empresaId) {
        Empresa empresa = service.getEmpresa(empresaId).orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada"));

        if (empresa != null) {
            return ResponseEntity.ok(empresa);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/empresas/{empresaId}")
    public ResponseEntity<Empresa> updateEmpresa(@PathVariable Long empresaId, @RequestBody EmpresaRequest request) {
        Empresa empresa = service.getEmpresa(empresaId).orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada"));
        Empresa empresaMapped = this.mapToEmpresa(request, empresa);

        service.saveEmpresa(empresaMapped);

        return empresa != null ? ResponseEntity.ok(empresa) : ResponseEntity.badRequest().build();
    }


    @DeleteMapping("/empresas/{empresaId}")
    public ResponseEntity<Void> deleteEmpresa(@PathVariable Long empresaId) {
        service.deleteEmpresa(empresaId);
        return ResponseEntity.noContent().build();
    }

    private Empresa mapToEmpresa(EmpresaRequest request, Empresa empresa) {
        empresa.setNombre(request.getNombre());
        empresa.setDescripcion(request.getDescripcion());
        empresa.setCorreoElectronico(request.getCorreoElectronico());

        return empresa;
    }
}