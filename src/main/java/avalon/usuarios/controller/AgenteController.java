package avalon.usuarios.controller;

import avalon.usuarios.mapper.UsuarioMapper;
import avalon.usuarios.model.pojo.Agente;
import avalon.usuarios.model.pojo.Broker;
import avalon.usuarios.model.request.AgenteRequest;
import avalon.usuarios.model.request.PartiallyUpdateUsuario;
import avalon.usuarios.service.AgenteService;
import avalon.usuarios.service.BrokerService;
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
public class AgenteController {

    @Autowired
    private AgenteService service;
    @Autowired
    private UsuarioMapper usuarioMapper;
    @Autowired
    private BrokerService brokerService;

    @PostMapping("/agentes")
    public ResponseEntity<Agente> createAgente(@RequestBody AgenteRequest request) {
        try {
            Agente agente = usuarioMapper.mapToUsuario(request, new Agente());
            Broker broker = brokerService.getBroker(request.getBrokerId());
            agente.setBroker(broker);

            Agente result = service.save(agente);
            return result.getId() != null ? ResponseEntity.status(HttpStatus.CREATED).body(result) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/agentes")
    public ResponseEntity<List<Agente>> getAgentes(@RequestParam(required = false) String estado) {
        List<Agente> agentes;
        if (estado == null)
            agentes = service.findAll();
        else
            agentes = service.findAllByEstado(estado);

        if (!agentes.isEmpty()) {
            return ResponseEntity.ok(agentes);
        } else {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }


    @GetMapping("/agentes/{agenteId}")
    public ResponseEntity<Agente> getAgente(@PathVariable Long agenteId) {
        Agente agente = service.findById(agenteId).orElseThrow(() -> new IllegalArgumentException("Agente no encontrado"));

        if (agente != null) {
            return ResponseEntity.ok(agente);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/agentes/{agenteId}")
    public ResponseEntity<Agente> partiallyUpdateAgente(@RequestBody PartiallyUpdateUsuario request, @PathVariable Long agenteId) {
        Agente agente = this.service.findById(agenteId).orElseThrow(() -> new IllegalArgumentException("Agente no encontrado"));

        Agente result = this.service.partiallyUpdateUsuario(request, agente);

        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/agentes/{agenteId}")
    public ResponseEntity<Agente> updateAgente(@PathVariable Long agenteId, @RequestBody AgenteRequest request) {
        Agente agente = this.service.findById(agenteId).orElseThrow(() -> new IllegalArgumentException("Agente no encontrado"));
        Agente agenteUpdate = usuarioMapper.mapToUsuario(request, agente);
        Broker broker = brokerService.getBroker(request.getBrokerId());
        agenteUpdate.setBroker(broker);

        service.save(agenteUpdate);
        return agenteUpdate != null ? ResponseEntity.ok(agenteUpdate) : ResponseEntity.badRequest().build();

    }

}
