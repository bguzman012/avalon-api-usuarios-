package avalon.usuarios.controller;

import avalon.usuarios.model.pojo.*;
import avalon.usuarios.model.request.*;
import avalon.usuarios.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ClientePolizaController {

    private final ClientesPolizaService service;
    @Autowired
    private ClienteService clienteService;
    @Autowired
    private AsesorService asesorService;
    @Autowired
    private AgenteService agenteService;
    @Autowired
    private PolizaService polizaService;

    @PostMapping("/clientesPolizas")
    public ResponseEntity<ClientePoliza> create(@RequestBody ClientePolizaRequest request) {
        try {
            ClientePoliza clientePoliza = this.mapToClientePoliza(request, new ClientePoliza());
            ClientePoliza result = service.savePoliza(clientePoliza);
            return result.getId() != null ? ResponseEntity.status(HttpStatus.CREATED).body(result) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/clientesPolizas")
    public ResponseEntity<List<ClientePoliza>> getPolizas() {
        List<ClientePoliza> clientesPolizas = service.getClientesPolizas();

        if (!clientesPolizas.isEmpty()) {
            return ResponseEntity.ok(clientesPolizas);
        } else {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @GetMapping("/polizas/{polizaId}/clientesPolizas")
    public ResponseEntity<List<ClientePoliza>> getClientesPolizasByPoliza(@PathVariable Long polizaId) {
        List<ClientePoliza> clientesPolizas = service.getClientesPolizasByPoliza(polizaId);

        if (!clientesPolizas.isEmpty()) {
            return ResponseEntity.ok(clientesPolizas);
        } else {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @GetMapping("/clientesPolizas/{clientePolizaId}")
    public ResponseEntity<ClientePoliza> getClientePoliza(@PathVariable Long clientePolizaId) {
        ClientePoliza clientePoliza = service.getClientePoliza(clientePolizaId).orElseThrow(() -> new IllegalArgumentException("Cliente Poliza no encontrada"));;

        if (clientePoliza != null) {
            return ResponseEntity.ok(clientePoliza);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/clientesPolizas/{clientePolizaId}")
    public ResponseEntity<ClientePoliza> updateClientePoliza(@PathVariable Long clientePolizaId, @RequestBody ClientePolizaRequest request) {
        ClientePoliza clientePoliza = service.getClientePoliza(clientePolizaId).orElseThrow(() -> new IllegalArgumentException("Cliente Poliza no encontrada"));
        ClientePoliza clientePolizaMapped  = this.mapToClientePoliza(request, clientePoliza);
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

        clientePolizaeference.setCliente(cliente);
        clientePolizaeference.setAsesor(asesor);
        clientePolizaeference.setAgente(agente);
        clientePolizaeference.setPoliza(poliza);
        return clientePolizaeference;
    }

}