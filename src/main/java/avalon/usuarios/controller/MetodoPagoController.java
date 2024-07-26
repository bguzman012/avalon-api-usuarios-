package avalon.usuarios.controller;

import avalon.usuarios.model.pojo.Aseguradora;
import avalon.usuarios.model.pojo.Membresia;
import avalon.usuarios.model.pojo.MetodoPago;
import avalon.usuarios.model.pojo.MetodoPago;
import avalon.usuarios.model.request.AseguradoraRequest;
import avalon.usuarios.model.request.MembresiaRequest;
import avalon.usuarios.model.request.MetodoPagoRequest;
import avalon.usuarios.service.MetodoPagoService;
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
public class MetodoPagoController {

    @Autowired
    private MetodoPagoService service;

    @PostMapping("/metodosPago")
    public ResponseEntity<MetodoPago> createMetodoPago(@RequestBody MetodoPagoRequest request) {
        try {
            MetodoPago metodoPago = this.mapToMetodoPago(request, new MetodoPago());
            service.saveMetodoPago(metodoPago);
            return metodoPago.getId() != null ? ResponseEntity.status(HttpStatus.CREATED).body(metodoPago) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/metodosPago/{metodoPagoId}")
    public ResponseEntity<MetodoPago> updateMetodoPago(@PathVariable Long metodoPagoId, @RequestBody MetodoPagoRequest request) {
        MetodoPago metodoPago = service.getMetodoPago(metodoPagoId).orElseThrow(() -> new IllegalArgumentException("Método de pago no encontrado"));
        MetodoPago metodoPagoMapped = this.mapToMetodoPago(request, metodoPago);
        this.service.saveMetodoPago(metodoPagoMapped);
        return metodoPagoMapped != null ? ResponseEntity.ok(metodoPagoMapped) : ResponseEntity.badRequest().build();
    }

    @GetMapping("/metodosPago")
    public ResponseEntity<List<MetodoPago>> getMetodosPago() {
        List<MetodoPago> metodosPago = service.searchMetodosPago();

        if (!metodosPago.isEmpty()) {
            return ResponseEntity.ok(metodosPago);
        } else {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @GetMapping("/metodosPago/{metodoPagoId}")
    public ResponseEntity<MetodoPago> getMetodoPago(@PathVariable Long metodoPagoId) {
        MetodoPago metodoPago = service.getMetodoPago(metodoPagoId).orElseThrow(() -> new IllegalArgumentException("Método de pago no encontrado"));

        if (metodoPago != null) {
            return ResponseEntity.ok(metodoPago);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private MetodoPago mapToMetodoPago(MetodoPagoRequest request, MetodoPago metodoPago) {
        metodoPago.setNombre(request.getNombre());
        metodoPago.setDescripcion(request.getDescripcion());
        return metodoPago;
    }


}
