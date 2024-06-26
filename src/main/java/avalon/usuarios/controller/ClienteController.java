package avalon.usuarios.controller;

import avalon.usuarios.mapper.UsuarioMapper;
import avalon.usuarios.model.pojo.Cliente;
import avalon.usuarios.model.request.ClienteRequest;
import avalon.usuarios.model.request.PartiallyUpdateUsuario;
import avalon.usuarios.service.ClienteService;
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
public class ClienteController {

    @Autowired
    private UsuarioMapper usuarioMapper;
    @Autowired
    private ClienteService service;

    @PostMapping("/clientes")
    public ResponseEntity<Cliente> createCliente(@RequestBody ClienteRequest request) {
        try {
            Cliente cliente = usuarioMapper.mapToUsuario(request, new Cliente());
            Cliente result = service.save(cliente);
            return result.getId() != null ? ResponseEntity.status(HttpStatus.CREATED).body(result) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/clientes")
    public ResponseEntity<List<Cliente>> getClientes(@RequestParam(required = false) String estado) {
        List<Cliente> clientes;
        if (estado == null || estado.isBlank())
            clientes = service.findAll();
        else
            clientes = service.findAllByEstado(estado);

        if (!clientes.isEmpty()) {
            return ResponseEntity.ok(clientes);
        } else {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @GetMapping("/clientes/{clienteId}")
    public ResponseEntity<Cliente> getCliente(@PathVariable Long clienteId) {
        Cliente cliente = service.findById(clienteId).orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

        if (cliente != null) {
            return ResponseEntity.ok(cliente);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/clientes/{clienteId}")
    public ResponseEntity<Cliente> partiallyUpdateCliente(@RequestBody PartiallyUpdateUsuario request, @PathVariable Long clienteId) {
        Cliente cliente = service.findById(clienteId).orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

        Cliente result = service.partiallyUpdateUsuario(request, cliente);

        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/clientes/{clienteId}")
    public ResponseEntity<Cliente> updateCliente(@PathVariable Long clienteId, @RequestBody ClienteRequest request) {
        Cliente cliente = service.findById(clienteId).orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
        Cliente clienteUpdate = usuarioMapper.mapToUsuario(request, cliente);

        service.save(clienteUpdate);
        return clienteUpdate != null ? ResponseEntity.ok(clienteUpdate) : ResponseEntity.badRequest().build();
    }
}
