package avalon.usuarios.controller;

import avalon.usuarios.model.pojo.*;
import avalon.usuarios.model.request.ClienteMembresiaRequest;
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
            ClienteMembresia result = service.saveClienteMembresia(clienteMembresia);
            return result.getId() != null ? ResponseEntity.status(HttpStatus.CREATED).body(result) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/clienteMembresias")
    public ResponseEntity<List<ClienteMembresia>> getUsuarioMembresias() {
        List<ClienteMembresia> clienteMembresias = service.getClientesMembresias();

        if (!clienteMembresias.isEmpty()) {
            return ResponseEntity.ok(clienteMembresias);
        } else {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @GetMapping("membresias/{membresiaId}/clienteMembresias")
    public ResponseEntity<List<ClienteMembresia>> getUsuarioMembresiasByMembresia(@PathVariable Long membresiaId) {
        List<ClienteMembresia> usuarioMembresiasResponseList = service.getClientesMembresiasByMembresia(membresiaId);

        if (!usuarioMembresiasResponseList.isEmpty()) {
            return ResponseEntity.ok(usuarioMembresiasResponseList);
        } else {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @GetMapping("clientes/{clienteId}/clienteMembresias")
    public ResponseEntity<List<ClienteMembresia>> getUsuarioMembresiasByUsuario(@PathVariable Long clienteId) {
        List<ClienteMembresia> usuarioMembresiasResponseList = service.getClientesMembresiasByCliente(clienteId);

        if (!usuarioMembresiasResponseList.isEmpty()) {
            return ResponseEntity.ok(usuarioMembresiasResponseList);
        } else {
            return ResponseEntity.ok(Collections.emptyList());
        }
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
