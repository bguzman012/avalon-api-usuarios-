package avalon.usuarios.controller;

import avalon.usuarios.model.pojo.Aseguradora;
import avalon.usuarios.model.pojo.Broker;
import avalon.usuarios.model.request.BrokerRequest;
import avalon.usuarios.model.request.PartiallyUpdateAseguradora;
import avalon.usuarios.model.request.PartiallyUpdateBroker;
import avalon.usuarios.model.response.PaginatedResponse;
import avalon.usuarios.service.BrokerService;
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
public class BrokerController {

    private final BrokerService service;

    @PostMapping("/brokers")
    public ResponseEntity<Broker> createBroker(@RequestBody BrokerRequest request) {
        try {
            request.setEstado("A");
            Broker broker = this.mapToBroker(request);
            service.createBroker(broker);
            return broker.getId() != null ? ResponseEntity.status(HttpStatus.CREATED).body(broker) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/brokers")
    public ResponseEntity<PaginatedResponse<Broker>> getBrokers(@RequestParam(required = false) String estado,
                                                   @RequestParam(required = false) String busqueda,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size,
                                                   @RequestParam(defaultValue = "createdDate") String sortField,
                                                   @RequestParam(defaultValue = "asc") String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("desc") ? Sort.by(sortField).descending() : Sort.by(sortField).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Broker> brokerPage = service.searchBrokers(estado, busqueda, pageable);

        List<Broker> brokers = brokerPage.getContent();
        long totalRecords = brokerPage.getTotalElements();

        PaginatedResponse<Broker> response = new PaginatedResponse<>(brokers, totalRecords);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/brokers/{brokerId}")
    public ResponseEntity<Broker> getBroker(@PathVariable Long brokerId) {
        Broker aseguradora = service.getBroker(brokerId);

        if (aseguradora != null) {
            return ResponseEntity.ok(aseguradora);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/brokers/{brokerId}")
    public ResponseEntity<Broker> updateBroker(@PathVariable Long brokerId, @RequestBody BrokerRequest request) {
        Broker broker = service.getBroker(brokerId);

        if (broker != null) {
            Broker brokerUpdate = service.updateBroker(broker, request);
            return brokerUpdate != null ? ResponseEntity.ok(brokerUpdate) : ResponseEntity.badRequest().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/brokers/{brokerId}")
    public ResponseEntity<Broker> partiallyUpdateBroker(@RequestBody PartiallyUpdateBroker request, @PathVariable Long brokerId) {
        Broker broker = service.getBroker(brokerId);
        Broker result = service.partiallyUpdateBroker(request.getEstado(), broker);

        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/brokers/{brokerId}")
    public ResponseEntity<Void> deleteBroker(@PathVariable Long brokerId) {
        service.deleteBroker(brokerId);
        return ResponseEntity.noContent().build();
    }

    private Broker mapToBroker(BrokerRequest request) {
        return Broker.builder()
                .nombre(request.getNombre())
                .correoElectronico(request.getCorreoElectronico())
                .estado(request.getEstado())
                .build();
    }

}