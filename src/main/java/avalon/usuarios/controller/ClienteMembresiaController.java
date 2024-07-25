package avalon.usuarios.controller;

import avalon.usuarios.model.pojo.*;
import avalon.usuarios.model.request.ClienteMembresiaRequest;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ClienteMembresiaController {

    private final ClienteMembresiaService service;
    @Autowired
    private ClienteService clienteService;
    @Autowired
    private MembresiaService membresiaService;
    @Autowired
    private AsesorService asesorService;

    @PostMapping("/clienteMembresias")
    public ResponseEntity<ClienteMembresia> createClienteMembresia(@RequestBody ClienteMembresiaRequest request) {
        try {
            ClienteMembresia clienteMembresia = this.mapToClienteMembresia(request, new ClienteMembresia());
            clienteMembresia.setEstado("A");
            ClienteMembresia result = service.saveClienteMembresia(clienteMembresia);
            return result.getId() != null ? ResponseEntity.status(HttpStatus.CREATED).body(result) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/clienteMembresias")
    public ResponseEntity<PaginatedResponse<ClienteMembresia>> getUsuarioMembresias(@RequestParam(required = false) String busqueda,
                                                                       @RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "10") int size,
                                                                       @RequestParam(defaultValue = "createdDate") String sortField,
                                                                       @RequestParam(defaultValue = "desc") String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("desc") ? Sort.by(sortField).descending() : Sort.by(sortField).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ClienteMembresia> clienteMembresiaPage = service.searchClientesMembresias(busqueda, pageable, null, null);

        List<ClienteMembresia> clienteMembresias = clienteMembresiaPage.getContent();
        long totalRecords = clienteMembresiaPage.getTotalElements();

        PaginatedResponse<ClienteMembresia> response = new PaginatedResponse<>(clienteMembresias, totalRecords);
        return ResponseEntity.ok(response);
    }

    @GetMapping("membresias/{membresiaId}/clienteMembresias")
    public ResponseEntity<PaginatedResponse<ClienteMembresia>> getUsuarioMembresiasByMembresia(@PathVariable Long membresiaId,
                                                                                  @RequestParam(required = false) String busqueda,
                                                                                  @RequestParam(defaultValue = "0") int page,
                                                                                  @RequestParam(defaultValue = "10") int size,
                                                                                  @RequestParam(defaultValue = "createdDate") String sortField,
                                                                                  @RequestParam(defaultValue = "desc") String sortOrder) {
        Membresia membresia = this.membresiaService.getMembresia(membresiaId).orElseThrow(() -> new IllegalArgumentException("Membresía no encontrada"));
        Sort sort = sortOrder.equalsIgnoreCase("desc") ? Sort.by(sortField).descending() : Sort.by(sortField).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ClienteMembresia> clienteMembresiaPage = service.searchClientesMembresias(busqueda, pageable, null, membresia);

        List<ClienteMembresia> clienteMembresias = clienteMembresiaPage.getContent();
        long totalRecords = clienteMembresiaPage.getTotalElements();

        PaginatedResponse<ClienteMembresia> response = new PaginatedResponse<>(clienteMembresias, totalRecords);
        return ResponseEntity.ok(response);
    }

    @GetMapping("clientes/{clienteId}/clienteMembresias")
    public ResponseEntity<PaginatedResponse<ClienteMembresia>> getUsuarioMembresiasByUsuario(@PathVariable Long clienteId,
                                                                                @RequestParam(required = false) String busqueda,
                                                                                @RequestParam(defaultValue = "0") int page,
                                                                                @RequestParam(defaultValue = "10") int size,
                                                                                @RequestParam(defaultValue = "createdDate") String sortField,
                                                                                @RequestParam(defaultValue = "desc") String sortOrder) {
        Cliente cliente = this.clienteService.findById(clienteId).orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
        Sort sort = sortOrder.equalsIgnoreCase("desc") ? Sort.by(sortField).descending() : Sort.by(sortField).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ClienteMembresia> clienteMembresiaPage = service.searchClientesMembresias(busqueda, pageable, cliente, null);

        List<ClienteMembresia> clienteMembresias = clienteMembresiaPage.getContent();
        long totalRecords = clienteMembresiaPage.getTotalElements();

        PaginatedResponse<ClienteMembresia> response = new PaginatedResponse<>(clienteMembresias, totalRecords);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/clienteMembresias/{clienteMembresiaId}")
    public ResponseEntity<ClienteMembresia> getUsuarioMembresia(@PathVariable Long clienteMembresiaId) {
        ClienteMembresia clienteMembresia = service.getClienteMembresia(clienteMembresiaId).orElseThrow(() -> new IllegalArgumentException("Cliente Membresía no encontrado"));;

        if (clienteMembresia != null) {
            return ResponseEntity.ok(clienteMembresia);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/clienteMembresias/{clienteMembresiaId}")
    public ResponseEntity<ClienteMembresia> updateUsuarioMembresia(@PathVariable Long clienteMembresiaId, @RequestBody ClienteMembresiaRequest request) {
        ClienteMembresia clienteMembresia = service.getClienteMembresia(clienteMembresiaId).orElseThrow(() -> new IllegalArgumentException("Cliente Membresía no encontrado"));
        ClienteMembresia clienteMembresiaMapped  = this.mapToClienteMembresia(request, clienteMembresia);
        this.service.saveClienteMembresia(clienteMembresiaMapped);
        return clienteMembresiaMapped != null ? ResponseEntity.ok(clienteMembresiaMapped) : ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/clienteMembresias/{clienteMembresiaId}")
    public ResponseEntity<Void> deleteUsuarioMembresia(@PathVariable Long clienteMembresiaId) {
        service.deleteClienteMembresia(clienteMembresiaId);
        return ResponseEntity.noContent().build();
    }

    private ClienteMembresia mapToClienteMembresia(ClienteMembresiaRequest request, ClienteMembresia clienteMembresiaReference) {
        Cliente cliente = this.clienteService.findById(request.getClienteId()).orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
        Membresia membresia = this.membresiaService.getMembresia(request.getMembresiaId()).orElseThrow(() -> new IllegalArgumentException("Membresía no encontrado"));
        Asesor asesor = this.asesorService.findById(request.getAsesorId()).orElseThrow(() -> new IllegalArgumentException("Asesor no encontrado"));

        clienteMembresiaReference.setCliente(cliente);
        clienteMembresiaReference.setMembresia(membresia);
        clienteMembresiaReference.setAsesor(asesor);
        clienteMembresiaReference.setFechaInicio(request.getFechaInicio());
        clienteMembresiaReference.setFechaFin(request.getFechaFin());
        return clienteMembresiaReference;
    }


}
