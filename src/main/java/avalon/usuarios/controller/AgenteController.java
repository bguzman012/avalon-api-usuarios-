package avalon.usuarios.controller;

import avalon.usuarios.mapper.UsuarioMapper;
import avalon.usuarios.model.pojo.Agente;
import avalon.usuarios.model.pojo.Asesor;
import avalon.usuarios.model.pojo.Broker;
import avalon.usuarios.model.request.AgenteRequest;
import avalon.usuarios.model.request.PartiallyUpdateUsuario;
import avalon.usuarios.model.response.PaginatedResponse;
import avalon.usuarios.service.AgenteService;
import avalon.usuarios.service.BrokerService;
import avalon.usuarios.util.ExceptionHandlerUtil;
import jakarta.mail.MessagingException;
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

import java.io.IOException;
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
    public ResponseEntity<?> createAgente(@RequestBody AgenteRequest request) {
        try {
            Agente agente = usuarioMapper.mapToUsuario(request, new Agente());
            Broker broker = brokerService.getBroker(request.getBrokerId());
            agente.setBroker(broker);

            Agente result = service.save(agente);
            return result.getId() != null ? ResponseEntity.status(HttpStatus.CREATED).body(result) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ExceptionHandlerUtil.userHandleException(e);
        }
    }

    @GetMapping("/agentes")
    public ResponseEntity<PaginatedResponse<Agente>> getAgentes(@RequestParam(required = false) String estado,
                                                                @RequestParam(required = false) String busqueda,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size,
                                                                @RequestParam(defaultValue = "createdDate") String sortField,
                                                                @RequestParam(defaultValue = "desc") String sortOrder
    ) {
        Sort sort = sortOrder.equalsIgnoreCase("desc") ? Sort.by(sortField).descending() : Sort.by(sortField).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Agente> asesorPage = service.searchAgentes(estado, busqueda, pageable);

        List<Agente> asesores = asesorPage.getContent();
        long totalRecords = asesorPage.getTotalElements();

        PaginatedResponse<Agente> response = new PaginatedResponse<>(asesores, totalRecords);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/brokers/{brokerId}/agentes")
    public ResponseEntity<PaginatedResponse<Agente>> getAgentesByBroker(@PathVariable Long brokerId,
                                                                        @RequestParam(required = false) String estado,
                                                                        @RequestParam(required = false) String busqueda,
                                                                        @RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "10") int size,
                                                                        @RequestParam(defaultValue = "createdDate") String sortField,
                                                                        @RequestParam(defaultValue = "desc") String sortOrder
    ) {
        Broker broker = brokerService.getBroker(brokerId);
        if (broker == null)
            return ResponseEntity.notFound().build();

        Sort sort = sortOrder.equalsIgnoreCase("desc") ? Sort.by(sortField).descending() : Sort.by(sortField).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Agente> asesorPage = service.searchAgentesByBroker(estado, busqueda, pageable, broker);

        List<Agente> asesores = asesorPage.getContent();
        long totalRecords = asesorPage.getTotalElements();

        PaginatedResponse<Agente> response = new PaginatedResponse<>(asesores, totalRecords);
        return ResponseEntity.ok(response);
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
    public ResponseEntity<Agente> partiallyUpdateAgente(@RequestBody PartiallyUpdateUsuario request, @PathVariable Long agenteId) throws MessagingException, IOException {
        Agente agente = this.service.findById(agenteId).orElseThrow(() -> new IllegalArgumentException("Agente no encontrado"));

        Agente result = this.service.partiallyUpdateUsuario(request, agente);

        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/agentes/{agenteId}")
    public ResponseEntity<?> updateAgente(@PathVariable Long agenteId, @RequestBody AgenteRequest request) throws MessagingException, IOException {
        try {
            Agente agente = this.service.findById(agenteId).orElseThrow(() -> new IllegalArgumentException("Agente no encontrado"));
            Agente agenteUpdate = usuarioMapper.mapToUsuario(request, agente);
            Broker broker = brokerService.getBroker(request.getBrokerId());
            agenteUpdate.setBroker(broker);

            service.save(agenteUpdate);
            return agenteUpdate != null ? ResponseEntity.ok(agenteUpdate) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ExceptionHandlerUtil.userHandleException(e);
        }
    }

}
