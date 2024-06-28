package avalon.usuarios.controller;

import avalon.usuarios.model.pojo.Beneficio;
import avalon.usuarios.model.pojo.CargaFamiliar;
import avalon.usuarios.model.pojo.ClientePoliza;
import avalon.usuarios.model.pojo.Membresia;
import avalon.usuarios.model.request.BeneficioRequest;
import avalon.usuarios.model.request.CargaFamiliarRequest;
import avalon.usuarios.service.BeneficioService;
import avalon.usuarios.service.CargaFamiliarService;
import avalon.usuarios.service.ClientesPolizaService;
import avalon.usuarios.service.MembresiaService;
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
public class CargaFamiliarController {

    private final CargaFamiliarService service;
    @Autowired
    private ClientesPolizaService clientesPolizaService;

    @PostMapping("/cargasFamiliares")
    public ResponseEntity<CargaFamiliar> createCargaFamiliar(@RequestBody CargaFamiliarRequest request) {
        try {
            CargaFamiliar cargaFamiliar = this.mapToCargaFamiliar(request, new CargaFamiliar());
            service.save(cargaFamiliar);
            return cargaFamiliar.getId() != null ? ResponseEntity.status(HttpStatus.CREATED).body(cargaFamiliar) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/cargasFamiliares")
    public ResponseEntity<List<CargaFamiliar>> getCargaFamiliars() {
        List<CargaFamiliar> cargasFamiliares = service.findAll();

        if (!cargasFamiliares.isEmpty()) {
            return ResponseEntity.ok(cargasFamiliares);
        } else {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @GetMapping("/clientesPolizas/{clientePolizaId}/cargasFamiliares")
    public ResponseEntity<List<CargaFamiliar>> getCargaFamiliarByClientePoliza(@PathVariable Long clientePolizaId) {
        ClientePoliza clientePoliza = this.clientesPolizaService.getClientePoliza(clientePolizaId).orElseThrow(() -> new IllegalArgumentException("Cliente Poliza no encontrada"));
        List<CargaFamiliar> cargasFamiliares = service.findAllByClientePoliza(clientePoliza);

        if (!cargasFamiliares.isEmpty()) {
            return ResponseEntity.ok(cargasFamiliares);
        } else {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @GetMapping("/cargasFamiliares/{cargaFamiliarId}")
    public ResponseEntity<CargaFamiliar> getCargaFamiliar(@PathVariable Long cargaFamiliarId) {
        CargaFamiliar cargaFamiliar = service.findById(cargaFamiliarId).orElseThrow(() -> new IllegalArgumentException("CargaFamiliar no encontrada"));

        if (cargaFamiliar != null) {
            return ResponseEntity.ok(cargaFamiliar);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/cargasFamiliares/{cargaFamiliarId}")
    public ResponseEntity<CargaFamiliar> updateCargaFamiliar(@PathVariable Long cargaFamiliarId, @RequestBody CargaFamiliarRequest request) {
        CargaFamiliar cargaFamiliar = service.findById(cargaFamiliarId).orElseThrow(() -> new IllegalArgumentException("CargaFamiliar no encontrada"));
        CargaFamiliar cargaFamiliarMapped = this.mapToCargaFamiliar(request, cargaFamiliar);
        service.save(cargaFamiliarMapped);

        return cargaFamiliarMapped.getId() != null ? ResponseEntity.ok(cargaFamiliarMapped) : ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/cargasFamiliares/{cargaFamiliarId}")
    public ResponseEntity<Void> deleteCargaFamiliar(@PathVariable Long cargaFamiliarId) {
        service.deleteById(cargaFamiliarId);
        return ResponseEntity.noContent().build();
    }

    private CargaFamiliar mapToCargaFamiliar(CargaFamiliarRequest request, CargaFamiliar cargaFamiliar) {
        ClientePoliza clientePoliza = this.clientesPolizaService.getClientePoliza(request.getClientePolizaId()).orElseThrow(() -> new IllegalArgumentException("Cliente Poliza no encontrada"));

        cargaFamiliar.setNombres(request.getNombres());
        cargaFamiliar.setApellidos(request.getApellidos());
        cargaFamiliar.setParentesco(request.getParentesco());
        cargaFamiliar.setCorreoElectronico(request.getCorreoElectronico());
        cargaFamiliar.setNumeroTelefono(request.getNumeroTelefono());
        cargaFamiliar.setUrlImagen(request.getUrlImagen());

        cargaFamiliar.setClientePoliza(clientePoliza);
        return cargaFamiliar;
    }


}