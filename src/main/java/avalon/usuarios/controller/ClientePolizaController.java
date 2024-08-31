package avalon.usuarios.controller;

import avalon.usuarios.config.AuditorAwareImpl;
import avalon.usuarios.model.pojo.*;
import avalon.usuarios.model.request.*;
import avalon.usuarios.model.response.PaginatedResponse;
import avalon.usuarios.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ClientePolizaController {

    @Autowired
    private AuditorAwareImpl auditorAware;
    private final ClientesPolizaService service;
    @Autowired
    private ClienteService clienteService;
    @Autowired
    private AsesorService asesorService;
    @Autowired
    private AgenteService agenteService;
    @Autowired
    private PolizaService polizaService;
    @Autowired
    private EmpresaService empresaService;

    @GetMapping("/clientesPolizas/excel")
    public ResponseEntity<byte[]> downloadExcel(@RequestParam(required = false) String busqueda,
                                                @RequestParam(defaultValue = "createdDate") String sortField,
                                                @RequestParam(defaultValue = "desc") String sortOrder) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = service.generateExcelClientesPolizas(busqueda, sortField, sortOrder);

        // Configurar las cabeceras de la respuesta
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=datos.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(byteArrayOutputStream.toByteArray());
    }

    @PostMapping("/clientesPolizas")
    public ResponseEntity<ClientePoliza> create(@RequestBody ClientePolizaRequest request) {
        try {
            ClientePoliza clientePoliza = this.mapToClientePoliza(request, new ClientePoliza());
            clientePoliza.setEstado("A");
            ClientePoliza result = service.savePoliza(clientePoliza);
            return result.getId() != null ? ResponseEntity.status(HttpStatus.CREATED).body(result) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

//    @GetMapping("/clientesPolizas/export")
//    public ResponseEntity<ClientePoliza> esportClientesPolizas(@RequestParam(required = false) String busqueda,
//                                                               @RequestParam(defaultValue = "createdDate") String sortField,
//                                                               @RequestParam(defaultValue = "desc") String sortOrder) {
//        Sort sort = sortOrder.equalsIgnoreCase("desc") ? Sort.by(sortField).descending() : Sort.by(sortField).ascending();
//        Pageable pageable = PageRequest.of(page, size, sort);
//
//        Page<ClientePoliza> clientePolizaPage = service.searchClienesPolizas(busqueda, pageable, null, null, this.getCurrentUser());
//
//        List<ClientePoliza> clientePolizas = clientePolizaPage.getContent();
//        long totalRecords = clientePolizaPage.getTotalElements();
//
//        PaginatedResponse<ClientePoliza> response = new PaginatedResponse<>(clientePolizas, totalRecords);
//        return ResponseEntity.ok(response);
//    }

    @GetMapping("/clientesPolizas")
    public ResponseEntity<PaginatedResponse<ClientePoliza>> getPolizas(@RequestParam(required = false) String busqueda,
                                                                       @RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "10") int size,
                                                                       @RequestParam(defaultValue = "createdDate") String sortField,
                                                                       @RequestParam(defaultValue = "desc") String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("desc") ? Sort.by(sortField).descending() : Sort.by(sortField).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ClientePoliza> clientePolizaPage = service.searchClienesPolizas(busqueda, pageable, null, null, this.getCurrentUser());

        List<ClientePoliza> clientePolizas = clientePolizaPage.getContent();
        long totalRecords = clientePolizaPage.getTotalElements();

        PaginatedResponse<ClientePoliza> response = new PaginatedResponse<>(clientePolizas, totalRecords);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/polizas/{polizaId}/clientesPolizas")
    public ResponseEntity<PaginatedResponse<ClientePoliza>> getClientesPolizasByPoliza(@PathVariable Long polizaId,
                                                                                       @RequestParam(required = false) String busqueda,
                                                                                       @RequestParam(defaultValue = "0") int page,
                                                                                       @RequestParam(defaultValue = "10") int size,
                                                                                       @RequestParam(defaultValue = "createdDate") String sortField,
                                                                                       @RequestParam(defaultValue = "desc") String sortOrder) {

        Poliza poliza = this.polizaService.getPoliza(polizaId).orElseThrow(() -> new IllegalArgumentException("Poliza no encontrado"));

        Sort sort = sortOrder.equalsIgnoreCase("desc") ? Sort.by(sortField).descending() : Sort.by(sortField).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ClientePoliza> clientePolizaPage = service.searchClienesPolizas(busqueda, pageable, null, poliza, this.getCurrentUser());

        List<ClientePoliza> clientePolizas = clientePolizaPage.getContent();
        long totalRecords = clientePolizaPage.getTotalElements();

        PaginatedResponse<ClientePoliza> response = new PaginatedResponse<>(clientePolizas, totalRecords);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/clientes/{clienteId}/clientesPolizas")
    public ResponseEntity<PaginatedResponse<ClientePoliza>> getClientesPolizasByCliente(@PathVariable Long clienteId,
                                                                                        @RequestParam(required = false) String busqueda,
                                                                                        @RequestParam(defaultValue = "0") int page,
                                                                                        @RequestParam(defaultValue = "10") int size,
                                                                                        @RequestParam(defaultValue = "createdDate") String sortField,
                                                                                        @RequestParam(defaultValue = "desc") String sortOrder) {
        Cliente cliente = this.clienteService.findById(clienteId).orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

        Sort sort = sortOrder.equalsIgnoreCase("desc") ? Sort.by(sortField).descending() : Sort.by(sortField).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ClientePoliza> clientePolizaPage = service.searchClienesPolizas(busqueda, pageable, cliente, null, this.getCurrentUser());

        List<ClientePoliza> clientePolizas = clientePolizaPage.getContent();
        long totalRecords = clientePolizaPage.getTotalElements();

        PaginatedResponse<ClientePoliza> response = new PaginatedResponse<>(clientePolizas, totalRecords);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/clientesPolizas/{clientePolizaId}")
    public ResponseEntity<ClientePoliza> getClientePoliza(@PathVariable Long clientePolizaId) {
        ClientePoliza clientePoliza = service.getClientePoliza(clientePolizaId).orElseThrow(() -> new IllegalArgumentException("Cliente Poliza no encontrada"));

        if (clientePoliza != null) {
            return ResponseEntity.ok(clientePoliza);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/clientesPolizas/{clientePolizaId}")
    public ResponseEntity<ClientePoliza> updateClientePoliza(@PathVariable Long clientePolizaId, @RequestBody ClientePolizaRequest request) {
        ClientePoliza clientePoliza = service.getClientePoliza(clientePolizaId).orElseThrow(() -> new IllegalArgumentException("Cliente Poliza no encontrada"));
        ClientePoliza clientePolizaMapped = this.mapToClientePoliza(request, clientePoliza);
        this.service.savePoliza(clientePolizaMapped);

        return clientePolizaMapped != null ? ResponseEntity.ok(clientePolizaMapped) : ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/clientesPolizas/{clientePolizaId}")
    public ResponseEntity<Void> deleteClientePoliza(@PathVariable Long clientePolizaId) {
        service.deleteClientePoliza(clientePolizaId);
        return ResponseEntity.noContent().build();
    }

    private ClientePoliza mapToClientePoliza(ClientePolizaRequest request, ClientePoliza clientePolizaeference) {
        Cliente cliente = this.clienteService.findById(request.getClienteId()).orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
        Asesor asesor = this.asesorService.findById(request.getAsesorId()).orElseThrow(() -> new IllegalArgumentException("Asesor no encontrado"));
        Agente agente = this.agenteService.findById(request.getAgenteId()).orElseThrow(() -> new IllegalArgumentException("Agente no encontrado"));
        Poliza poliza = this.polizaService.getPoliza(request.getPolizaId()).orElseThrow(() -> new IllegalArgumentException("Poliza no encontrado"));

        Empresa empresa = null;
        if (request.getEmpresaId()!= null)
            empresa = this.empresaService.getEmpresa(request.getEmpresaId()).orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada"));

        clientePolizaeference.setCliente(cliente);
        clientePolizaeference.setAsesor(asesor);
        clientePolizaeference.setAgente(agente);
        clientePolizaeference.setPoliza(poliza);
        clientePolizaeference.setEmpresa(empresa);
        clientePolizaeference.setFechaInicio(request.getFechaInicio());
        clientePolizaeference.setFechaFin(request.getFechaFin());
        clientePolizaeference.setNumeroCertificado(request.getNumeroCertificado());
        clientePolizaeference.setTipo("TITULAR");

        return clientePolizaeference;
    }

    private Usuario getCurrentUser() {
        Optional<String> currentUser = this.auditorAware.getCurrentAuditor();

        return currentUser.map(s -> this.clienteService.findByNombreUsuario(s)).orElse(null);

    }
}