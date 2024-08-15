package avalon.usuarios.controller;

import avalon.usuarios.mapper.UsuarioMapper;
import avalon.usuarios.model.pojo.*;
import avalon.usuarios.model.request.BeneficioRequest;
import avalon.usuarios.model.request.CargaFamiliarRequest;
import avalon.usuarios.model.request.ClientePolizaRequest;
import avalon.usuarios.model.request.UpdateCargaFamiliarRequest;
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

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CargaFamiliarController {

    private final CargaFamiliarService service;
    @Autowired
    private ClienteService clienteService;
    @Autowired
    private ClientesPolizaService clientesPolizaService;
    @Autowired
    private UsuarioMapper usuarioMapper;

    @PostMapping("/cargasFamiliares")
    public ResponseEntity<ClientePoliza> createCargaFamiliar(@RequestBody CargaFamiliarRequest request) {
        try {
            Cliente cliente;
            if (request.getClienteId() == null)
                cliente = usuarioMapper.mapToUsuario(request, new Cliente(), new Direccion());
            else
                cliente = this.clienteService.findById(request.getClienteId()).orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

            ClientePoliza clientePoliza= this.mapToClientePolizaCargaFamiliar(request, cliente, new ClientePoliza());
            clientesPolizaService.savePoliza(clientePoliza);
            return clientePoliza.getId() != null ? ResponseEntity.status(HttpStatus.CREATED).body(clientePoliza) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

//    @GetMapping("/cargasFamiliares")
//    public ResponseEntity<List<CargaFamiliar>> getCargaFamiliars() {
//        List<CargaFamiliar> cargasFamiliares = service.findAll();
//
//        if (!cargasFamiliares.isEmpty()) {
//            return ResponseEntity.ok(cargasFamiliares);
//        } else {
//            return ResponseEntity.ok(Collections.emptyList());
//        }
//    }

    @GetMapping("/clientesPolizas/{clientePolizaId}/cargasFamiliares")
    public ResponseEntity<PaginatedResponse<ClientePoliza>> getCargaFamiliarByClientePoliza(@PathVariable Long clientePolizaId,
                                                                                            @RequestParam(required = false) String busqueda,
                                                                                            @RequestParam(defaultValue = "0") int page,
                                                                                            @RequestParam(defaultValue = "10") int size,
                                                                                            @RequestParam(defaultValue = "createdDate") String sortField,
                                                                                            @RequestParam(defaultValue = "desc") String sortOrder) {
        ClientePoliza clientePoliza = this.clientesPolizaService.getClientePoliza(clientePolizaId).orElseThrow(() -> new IllegalArgumentException("Cliente Poliza no encontrada"));
        Sort sort = sortOrder.equalsIgnoreCase("desc") ? Sort.by(sortField).descending() : Sort.by(sortField).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ClientePoliza> clientePolizaPage = service.searchCargasByClientePoliza(busqueda, clientePoliza, pageable);

        List<ClientePoliza> clientePolizas = clientePolizaPage.getContent();
        long totalRecords = clientePolizaPage.getTotalElements();

        PaginatedResponse<ClientePoliza> response = new PaginatedResponse<>(clientePolizas, totalRecords);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cargasFamiliares/{clientePolizaId}")
    public ResponseEntity<ClientePoliza> getClientePoliza(@PathVariable Long clientePolizaId) {
        ClientePoliza clientePoliza = this.clientesPolizaService.getClientePoliza(clientePolizaId).orElseThrow(() -> new IllegalArgumentException("Cliente poliza titular no encontrado"));

        if (clientePoliza != null) {
            return ResponseEntity.ok(clientePoliza);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/cargasFamiliares/{clientePolizaId}")
    public ResponseEntity<ClientePoliza> updateCargaFamiliar(@PathVariable Long clientePolizaId, @RequestBody UpdateCargaFamiliarRequest request) {
        ClientePoliza clientePoliza = this.clientesPolizaService.getClientePoliza(clientePolizaId).orElseThrow(() -> new IllegalArgumentException("Cliente poliza titular no encontrado"));
        Cliente cliente = this.clienteService.findById(request.getClienteId()).orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

        clientePoliza.setParentesco(request.getParentesco());
        clientePoliza.setCliente(cliente);
        clientePoliza.setNumeroCertificado(request.getNumeroCertificado());

        clientesPolizaService.savePoliza(clientePoliza);
        return clientePoliza.getId() != null ? ResponseEntity.ok(clientePoliza) : ResponseEntity.badRequest().build();
    }

//    private CargaFamiliar mapToCargaFamiliar(CargaFamiliarRequest request, CargaFamiliar clientePoliza) {
//        ClientePoliza clientePoliza = this.clientesPolizaService.getClientePoliza(request.getClientePolizaId()).orElseThrow(() -> new IllegalArgumentException("Cliente Poliza no encontrada"));
//
//        clientePoliza.setNombres(request.getNombres());
//        clientePoliza.setNombresDos(request.getNombresDos());
//        clientePoliza.setApellidos(request.getApellidos());
//        clientePoliza.setApellidosDos(request.getApellidosDos());
//        clientePoliza.setParentesco(request.getParentesco());
//        clientePoliza.setCorreoElectronico(request.getCorreoElectronico());
//        clientePoliza.setNumeroTelefono(request.getNumeroTelefono());
//        clientePoliza.setUrlImagen(request.getUrlImagen());
//
//        clientePoliza.setClientePoliza(clientePoliza);
//        return clientePoliza;
//    }

    private ClientePoliza mapToClientePolizaCargaFamiliar(CargaFamiliarRequest request, Cliente cliente, ClientePoliza clientePolizaeference) {
        ClientePoliza clientePolizaTitular = this.clientesPolizaService.getClientePoliza(request.getClientePolizaTitularId()).orElseThrow(() -> new IllegalArgumentException("Cliente poliza titular no encontrado"));
        Asesor asesor = clientePolizaTitular.getAsesor();
        Agente agente = clientePolizaTitular.getAgente();
        Poliza poliza = clientePolizaTitular.getPoliza();

        clientePolizaeference.setTitular(clientePolizaTitular);
        clientePolizaeference.setCliente(cliente);
        clientePolizaeference.setAsesor(asesor);
        clientePolizaeference.setAgente(agente);
        clientePolizaeference.setPoliza(poliza);
        clientePolizaeference.setFechaInicio(clientePolizaTitular.getFechaInicio());
        clientePolizaeference.setFechaFin(clientePolizaTitular.getFechaFin());
        clientePolizaeference.setNumeroCertificado(request.getNumeroCertificado());
        clientePolizaeference.setParentesco(request.getParentesco());
        clientePolizaeference.setEstado("A");
        clientePolizaeference.setTipo("DEPENDIENTE");

        return clientePolizaeference;
    }


}