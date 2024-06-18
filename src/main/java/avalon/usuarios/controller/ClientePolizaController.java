package avalon.usuarios.controller;

import avalon.usuarios.model.pojo.ClientePoliza;
import avalon.usuarios.model.pojo.Poliza;
import avalon.usuarios.model.request.CreateClientePolizaRequest;
import avalon.usuarios.model.request.CreatePolizaRequest;
import avalon.usuarios.model.request.UpdateClientePolizaRequest;
import avalon.usuarios.model.request.UpdatePolizaRequest;
import avalon.usuarios.service.ClientesPolizaServiceImpl;
import avalon.usuarios.service.PolizasServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ClientePolizaController {

    private final ClientesPolizaServiceImpl service;

    @PostMapping("/clientesPolizas")
    public ResponseEntity<ClientePoliza> create(@RequestBody CreateClientePolizaRequest request) {
        try {
            ClientePoliza result = service.createClientePoliza(request);
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


//    @GetMapping("/usuarios/{usuarioId}/aseguradoras")
//    public ResponseEntity<List<CreateAseguradoraResponse>> getMembresiasByUsuario(@PathVariable Long usuarioId, @RequestParam(required = false) String estado) {
//        List<CreateAseguradoraResponse> aseguradoras = service.getAseguradoraByUsuarioAndEstado(usuarioId, estado);
//
//        if (!aseguradoras.isEmpty()) {
//            return ResponseEntity.ok(aseguradoras);
//        } else {
//            return ResponseEntity.ok(Collections.emptyList());
//        }
//    }

    @GetMapping("/clientesPolizas/{clientePolizaId}")
    public ResponseEntity<ClientePoliza> getClientePoliza(@PathVariable Long clientePolizaId) {
        ClientePoliza clientePoliza = service.getClientePoliza(clientePolizaId);

        if (clientePoliza != null) {
            return ResponseEntity.ok(clientePoliza);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/clientesPolizas/{clientePolizaId}")
    public ResponseEntity<ClientePoliza> updateClientePoliza(@PathVariable Long clientePolizaId, @RequestBody UpdateClientePolizaRequest request) {
        ClientePoliza clientePoliza = service.getClientePoliza(clientePolizaId);

        if (clientePoliza != null) {
            ClientePoliza clientePolizaUpdate = service.updateClientePoliza(clientePoliza, request);
            return clientePolizaUpdate != null ? ResponseEntity.ok(clientePolizaUpdate) : ResponseEntity.badRequest().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

//    @PatchMapping("/aseguradoras/{aseguradoraId}")
//    public ResponseEntity<Aseguradora> partiallyUpdateAseguradora(@RequestBody PartiallyUpdateAseguradora request, @PathVariable Long aseguradoraId) {
//        Aseguradora result = service.partiallyUpdateAseguradora(request, aseguradoraId);
//
//        if (result != null) {
//            return ResponseEntity.ok(result);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }

    @DeleteMapping("/clientesPolizas/{clientePolizaId}")
    public ResponseEntity<Void> deleteClientePoliza(@PathVariable Long clientePolizaId) {
        service.deleteClientePoliza(clientePolizaId);
        return ResponseEntity.noContent().build();
    }

}