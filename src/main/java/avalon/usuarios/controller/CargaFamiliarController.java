package avalon.usuarios.controller;

import avalon.usuarios.mapper.UsuarioMapper;
import avalon.usuarios.model.pojo.*;
import avalon.usuarios.model.request.BeneficioRequest;
import avalon.usuarios.model.request.CargaFamiliarRequest;
import avalon.usuarios.model.request.ClientePolizaRequest;
import avalon.usuarios.model.request.UpdateCargaFamiliarRequest;
import avalon.usuarios.model.response.PaginatedResponse;
import avalon.usuarios.service.*;
import avalon.usuarios.service.mail.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
    private ClienteMembresiaService clienteMembresiaService;
    @Autowired
    private UsuarioMapper usuarioMapper;
    @Autowired
    private MailService mailService;

    @PostMapping("/cargasFamiliares")
    public ResponseEntity<ClientePoliza> createCargaFamiliar(@RequestBody CargaFamiliarRequest request) {
        try {
            Cliente cliente;
            if (request.getClienteId() == null) {
                cliente = usuarioMapper.mapToUsuarioFromCargaFamiliar(request, new Cliente(), new Direccion());
                this.clienteService.save(cliente);

                String textoMail = "<p><b>" + cliente.getNombres() + " " + cliente.getNombresDos() + " "
                        + cliente.getApellidos() + " " + cliente.getApellidosDos() + " [" + cliente.getNombreUsuario() +
                        "]</b></p>" +
                        "<p>Su usuario ha sido creado y aprobado con éxito por parte del Administrador de Avalon. La contraseña temporal para su primer " +
                        "inicio de sesión es la siguiente: </p>" +
                        "<p><b>" + cliente.getContraseniaTemporal() + "</b></p>";

                this.mailService.sendHtmlEmail(cliente.getCorreoElectronico(), "Avalon Usuario Creado", textoMail);

                ClienteMembresia clienteMembresiaTitular = this.clienteMembresiaService.getClienteMembresia(request.getClienteMembresiaTitularId()).orElseThrow(() -> new IllegalArgumentException("Cliente membresía titular no encontrada"));
                ClienteMembresia clienteMembresiaDependiente = new ClienteMembresia();
                clienteMembresiaDependiente.setCodigo(request.getCodigoMembresia());
                clienteMembresiaDependiente.setMembresia(clienteMembresiaTitular.getMembresia());
                clienteMembresiaDependiente.setCliente(cliente);
                clienteMembresiaDependiente.setAsesor(clienteMembresiaTitular.getAsesor());
                clienteMembresiaDependiente.setFechaInicio(clienteMembresiaTitular.getFechaInicio());
                clienteMembresiaDependiente.setFechaFin(clienteMembresiaTitular.getFechaFin());
                clienteMembresiaDependiente.setEstado(clienteMembresiaTitular.getEstado());

                this.clienteMembresiaService.saveClienteMembresia(clienteMembresiaDependiente);
            }else
                cliente = this.clienteService.findById(request.getClienteId()).orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

            ClientePoliza clientePoliza= this.mapToClientePolizaCargaFamiliar(request, cliente, new ClientePoliza());
            clientesPolizaService.savePoliza(clientePoliza);
            return clientePoliza.getId() != null ? ResponseEntity.status(HttpStatus.CREATED).body(clientePoliza) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/cargasFamiliares/excel")
    public ResponseEntity<byte[]> downloadExcel(@RequestParam(required = false) String clientePoliza,
                                                @RequestParam(required = false) String busqueda,
                                                @RequestParam(defaultValue = "createdDate") String sortField,
                                                @RequestParam(defaultValue = "desc") String sortOrder) throws IOException {
        ClientePoliza clientePolizaObj = null;
        if (clientePoliza != null && !clientePoliza.isEmpty())
            clientePolizaObj = this.clientesPolizaService.getClientePoliza(Long.valueOf(clientePoliza)).orElseThrow(() -> new IllegalArgumentException("Cliente Poliza no encontrada"));

        ByteArrayOutputStream byteArrayOutputStream = service.generateExcelClientesPolizas(clientePolizaObj, busqueda, sortField, sortOrder);

        // Configurar las cabeceras de la respuesta
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=datos.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(byteArrayOutputStream.toByteArray());
    }


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