package avalon.usuarios.controller;

import avalon.usuarios.config.AuditorAwareImpl;
import avalon.usuarios.mapper.UsuarioMapper;
import avalon.usuarios.model.pojo.*;
import avalon.usuarios.model.request.*;
import avalon.usuarios.model.response.MigracionResponse;
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
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ClientePolizaController {

    @Autowired
    private AuditorAwareImpl auditorAware;
    private final ClientesPolizaService service;
    @Autowired
    private ClienteService clienteService;
    @Autowired
    private AsesorService asesorService;
    @Autowired
    private ClienteMembresiaService clienteMembresiaService;
    @Autowired
    private AgenteService agenteService;
    @Autowired
    private AseguradoraService aseguradoraService;
    @Autowired
    private PolizaService polizaService;
    @Autowired
    private MembresiaService membresiaService;
    @Autowired
    private EmpresaService empresaService;
    @Autowired
    private UsuarioMapper usuarioMapper;
    @Autowired
    private PaisService paisService;
    @Autowired
    private EstadosService estadosService;
    @Autowired
    private MailService mailService;

    @GetMapping("/clientesPolizas/excel")
    public ResponseEntity<byte[]> downloadExcel(@RequestParam(required = false) String busqueda,
                                                @RequestParam(defaultValue = "createdDate") String sortField,
                                                @RequestParam(defaultValue = "desc") String sortOrder) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = service.generateExcelClientesPolizas(busqueda, sortField, sortOrder);

        // Configurar las cabeceras de la respuesta
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=datos.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(byteArrayOutputStream.toByteArray());
    }

    @PostMapping("/clientesPolizas")
    public ResponseEntity<ClientePoliza> create(@RequestBody ClientePolizaRequest request) {
        try {
            ClientePoliza clientePoliza = this.mapToClientePoliza(request, new ClientePoliza());
            clientePoliza.setEstado("A");
            ClientePoliza result = service.savePoliza(clientePoliza);
            return result.getId() != null ? ResponseEntity.status(HttpStatus.CREATED).body(result) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/clientesPolizas/migracion")
    public ResponseEntity<MigracionResponse> createMigracion(@RequestBody MigracionClientePolizaRequest request) {
        try {
            ClientePoliza clientePoliza = new ClientePoliza();
            MigracionResponse migracionResponse;

            Aseguradora aseguradora = this.aseguradoraService.getAseguradoraByNombre(request.getSeguro()).orElse(null);
            if (aseguradora == null) {
                migracionResponse = new MigracionResponse(500, "Aseguradora no encontrada en la base de datos", "ERROR");
                return ResponseEntity.status(HttpStatus.CREATED).body(migracionResponse);
            }

            Asesor asesor = this.asesorService.findByCorreo(request.getCorreoAsesor()).orElse(null);
            if (asesor == null) {
                migracionResponse = new MigracionResponse(500, "Asesor no encontrado en la base de datos", "ERROR");
                return ResponseEntity.status(HttpStatus.CREATED).body(migracionResponse);
            }

            Agente agente = this.agenteService.findByCorreo(request.getCorreoAgente()).orElse(null);
            if (agente == null) {
                migracionResponse = new MigracionResponse(500, "Agente no encontrado en la base de datos", "ERROR");
                return ResponseEntity.status(HttpStatus.CREATED).body(migracionResponse);
            }

            Poliza poliza = this.polizaService.getPolizaByNameAndAseguradora(request.getPoliza(), aseguradora).orElse(null);
            if (poliza == null) {
                migracionResponse = new MigracionResponse(500, "Poliza no encontrada en la base de datos", "ERROR");
                return ResponseEntity.status(HttpStatus.CREATED).body(migracionResponse);
            }

            Pais pais = paisService.findByNombre(request.getPais()).orElse(null);
            if (pais == null) {
                migracionResponse = new MigracionResponse(500, "País no encontrada en la base de datos", "ERROR");
                return ResponseEntity.status(HttpStatus.CREATED).body(migracionResponse);
            }

            Estado estado = estadosService.findByNombre(request.getEstado()).orElse(null);
            if (estado == null) {
                migracionResponse = new MigracionResponse(500, "Estado no encontrado en la base de datos", "ERROR");
                return ResponseEntity.status(HttpStatus.CREATED).body(migracionResponse);
            }

            // La empresa puede ser nulo, si viene un parsmetro de empresa, primero buscamos si no encontramos, creamos
            Empresa empresa = null;
            if (request.getEmpresa() != null && !request.getEmpresa().isEmpty()) {
                empresa = this.empresaService.getEmpresaByNombre(request.getEmpresa()).orElse(null);
                if (empresa == null) {
                    Empresa empresaCreated = new Empresa();
                    empresaCreated.setNombre(request.getEmpresa());
                    empresaCreated.setDescripcion(request.getEmpresa());
                    this.empresaService.saveEmpresa(empresaCreated);
                    empresa = empresaCreated;
                }
            }

            if (!request.getTipoPoliza().equals("DEPENDIENTE") && !request.getTipoPoliza().equals("TITULAR")) {
                migracionResponse = new MigracionResponse(500, "El tipo de poliza solo puede ser DEPENDIENTE o TITULAR.", "ERROR");
                return ResponseEntity.status(HttpStatus.CREATED).body(migracionResponse);
            }

            // Si es dependiente busca la poliza del titular para relacionarle
            if (request.getTipoPoliza().equals("DEPENDIENTE")) {
                ClientePoliza clientePolizaTitular = this.service.getClientePolizaTitularByCertificado(request.getNumeroCertificado()).orElse(null);
                if (clientePolizaTitular == null) {
                    migracionResponse = new MigracionResponse(500, "El titular de la poliza con numero certificado " + request.getNumeroCertificado() + " no existe.", "ERROR");
                    return ResponseEntity.status(HttpStatus.CREATED).body(migracionResponse);
                }

                clientePoliza.setTitular(clientePolizaTitular);
                clientePoliza.setTipo("DEPENDIENTE");
            }

            if (request.getTipoPoliza().equals("TITULAR")) {
                Boolean existClientePolizaTitularByCertificado = service.existClientePolizaTitular(request.getNumeroCertificado(), "TITULAR");
                if (existClientePolizaTitularByCertificado) {
                    migracionResponse = new MigracionResponse(500, "Esta poliza ya tiene un titular, no es opsible agregar otro.", "ERROR");
                    return ResponseEntity.status(HttpStatus.CREATED).body(migracionResponse);
                }

                clientePoliza.setTitular(null);
                clientePoliza.setTipo("TITULAR");
            }

            Cliente cliente = this.clienteService.findClienteByCorreoElectronico(request.getCorreoElectronico()).orElse(null);
            if (cliente == null) {
                cliente = usuarioMapper.mapToClienteFromMigracionClientePoliza(request, new Cliente(), new Direccion());
                this.clienteService.save(cliente);

                if (cliente.tiene18OMasAnios()) {
                    String textoMail = "<p><b>" + cliente.getNombres() + " " + cliente.getNombresDos() + " "
                            + cliente.getApellidos() + " " + cliente.getApellidosDos() + " [" + cliente.getNombreUsuario() +
                            "]</b></p>" +
                            "<p>Su usuario ha sido creado y aprobado con éxito por parte del Administrador de Avalon. La contraseña temporal para su primer " +
                            "inicio de sesión es la siguiente: </p>" +
                            "<p><b>" + cliente.getContraseniaTemporal() + "</b></p>";

                    this.mailService.sendHtmlEmail(cliente.getCorreoElectronico(), "Avalon Usuario Creado", textoMail);
                }
            }

            if (request.getMembresia() != null && !request.getMembresia().isEmpty()) {
                Membresia membresia = this.membresiaService.getMembresiaByName(request.getMembresia()).orElse(null);
                if (membresia == null) {
                    migracionResponse = new MigracionResponse(500, "Membresía no encontrada en la base de datos", "ERROR");
                    return ResponseEntity.status(HttpStatus.CREATED).body(migracionResponse);
                }

                ClienteMembresia clienteMembresiaDependiente = new ClienteMembresia();
                clienteMembresiaDependiente.setCodigo(request.getMembresiaCodigo());
                clienteMembresiaDependiente.setMembresia(membresia);
                clienteMembresiaDependiente.setCliente(cliente);
                clienteMembresiaDependiente.setAsesor(asesor);
                clienteMembresiaDependiente.setFechaInicio(request.getFechaInicioMembresia());
                clienteMembresiaDependiente.setFechaFin(request.getFechaExpiracionMembresia());
                clienteMembresiaDependiente.setEstado("A");

                this.clienteMembresiaService.saveClienteMembresia(clienteMembresiaDependiente);
            }


            clientePoliza.setCliente(cliente);
            clientePoliza.setEmpresa(empresa);
            clientePoliza.setAsesor(asesor);
            clientePoliza.setAgente(agente);
            clientePoliza.setPoliza(poliza);
            clientePoliza.setEstado("A");
            clientePoliza.setFechaInicio(request.getFechaInicioPoliza());
            clientePoliza.setFechaFin(request.getFechaExpiracionPoliza());
            clientePoliza.setNumeroCertificado(request.getNumeroCertificado());
            clientePoliza.setParentesco(request.getParentesco());

            this.service.savePoliza(clientePoliza);

            migracionResponse = new MigracionResponse(200, "Cliente poliza migrado con éxito", "MIGRADO");
            return ResponseEntity.status(HttpStatus.CREATED).body(migracionResponse);
        } catch (
                Exception e) {
            MigracionResponse migracionResponse = new MigracionResponse(500, e.getMessage(), "ERROR");
            return ResponseEntity.status(HttpStatus.CREATED).body(migracionResponse);
        }

    }

    @GetMapping("/clientesPolizas")
    public ResponseEntity<PaginatedResponse<ClientePoliza>> getPolizas(@RequestParam(required = false) String busqueda,
                                                                       @RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "10") int size,
                                                                       @RequestParam(defaultValue = "createdDate") String sortField,
                                                                       @RequestParam(defaultValue = "desc") String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("desc") ? Sort.by(sortField).descending() : Sort.by(sortField).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ClientePoliza> clientePolizaPage = service.searchClienesPolizas(busqueda, pageable, null, null, this.getCurrentUser());

        List<ClientePoliza> clientePolizas = clientePolizaPage.getContent();
        long totalRecords = clientePolizaPage.getTotalElements();

        PaginatedResponse<ClientePoliza> response = new PaginatedResponse<>(clientePolizas, totalRecords);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/polizas/{polizaId}/clientesPolizas")
    public ResponseEntity<PaginatedResponse<ClientePoliza>> getClientesPolizasByPoliza(@PathVariable Long polizaId,
                                                                                       @RequestParam(required = false) String busqueda,
                                                                                       @RequestParam(defaultValue = "0") int page,
                                                                                       @RequestParam(defaultValue = "10") int size,
                                                                                       @RequestParam(defaultValue = "createdDate") String sortField,
                                                                                       @RequestParam(defaultValue = "desc") String sortOrder) {

        Poliza poliza = this.polizaService.getPoliza(polizaId).orElseThrow(() -> new IllegalArgumentException("Poliza no encontrado"));

        Sort sort = sortOrder.equalsIgnoreCase("desc") ? Sort.by(sortField).descending() : Sort.by(sortField).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ClientePoliza> clientePolizaPage = service.searchClienesPolizas(busqueda, pageable, null, poliza, this.getCurrentUser());

        List<ClientePoliza> clientePolizas = clientePolizaPage.getContent();
        long totalRecords = clientePolizaPage.getTotalElements();

        PaginatedResponse<ClientePoliza> response = new PaginatedResponse<>(clientePolizas, totalRecords);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/clientes/{clienteId}/clientesPolizas")
    public ResponseEntity<PaginatedResponse<ClientePoliza>> getClientesPolizasByCliente(@PathVariable Long clienteId,
                                                                                        @RequestParam(required = false) String busqueda,
                                                                                        @RequestParam(defaultValue = "0") int page,
                                                                                        @RequestParam(defaultValue = "10") int size,
                                                                                        @RequestParam(defaultValue = "createdDate") String sortField,
                                                                                        @RequestParam(defaultValue = "desc") String sortOrder) {
        Cliente cliente = this.clienteService.findById(clienteId).orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

        Sort sort = sortOrder.equalsIgnoreCase("desc") ? Sort.by(sortField).descending() : Sort.by(sortField).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ClientePoliza> clientePolizaPage = service.searchClienesPolizas(busqueda, pageable, cliente, null, this.getCurrentUser());

        List<ClientePoliza> clientePolizas = clientePolizaPage.getContent();
        long totalRecords = clientePolizaPage.getTotalElements();

        PaginatedResponse<ClientePoliza> response = new PaginatedResponse<>(clientePolizas, totalRecords);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/clientesPolizas/{clientePolizaId}")
    public ResponseEntity<ClientePoliza> getClientePoliza(@PathVariable Long clientePolizaId) {
        ClientePoliza clientePoliza = service.getClientePoliza(clientePolizaId).orElseThrow(() -> new IllegalArgumentException("Cliente Poliza no encontrada"));

        if (clientePoliza != null) {
            return ResponseEntity.ok(clientePoliza);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/clientesPolizas/{clientePolizaId}")
    public ResponseEntity<ClientePoliza> updateClientePoliza(@PathVariable Long clientePolizaId, @RequestBody ClientePolizaRequest request) {
        ClientePoliza clientePoliza = service.getClientePoliza(clientePolizaId).orElseThrow(() -> new IllegalArgumentException("Cliente Poliza no encontrada"));
        ClientePoliza clientePolizaMapped = this.mapToClientePoliza(request, clientePoliza);
        this.service.savePoliza(clientePolizaMapped);

        return clientePolizaMapped != null ? ResponseEntity.ok(clientePolizaMapped) : ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/clientesPolizas/{clientePolizaId}")
    public ResponseEntity<Void> deleteClientePoliza(@PathVariable Long clientePolizaId) {
        service.deleteClientePoliza(clientePolizaId);
        return ResponseEntity.noContent().build();
    }

    private ClientePoliza mapToClientePoliza(ClientePolizaRequest request, ClientePoliza clientePolizaeference) {
        Cliente cliente = this.clienteService.findById(request.getClienteId()).orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
        Asesor asesor = this.asesorService.findById(request.getAsesorId()).orElseThrow(() -> new IllegalArgumentException("Asesor no encontrado"));
        Agente agente = this.agenteService.findById(request.getAgenteId()).orElseThrow(() -> new IllegalArgumentException("Agente no encontrado"));
        Poliza poliza = this.polizaService.getPoliza(request.getPolizaId()).orElseThrow(() -> new IllegalArgumentException("Poliza no encontrado"));

        Empresa empresa = null;
        if (request.getEmpresaId() != null)
            empresa = this.empresaService.getEmpresa(request.getEmpresaId()).orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada"));

        clientePolizaeference.setCliente(cliente);
        clientePolizaeference.setAsesor(asesor);
        clientePolizaeference.setAgente(agente);
        clientePolizaeference.setPoliza(poliza);
        clientePolizaeference.setEmpresa(empresa);
        clientePolizaeference.setFechaInicio(request.getFechaInicio());
        clientePolizaeference.setFechaFin(request.getFechaFin());
        clientePolizaeference.setNumeroCertificado(request.getNumeroCertificado());
        clientePolizaeference.setTipo("TITULAR");

        return clientePolizaeference;
    }


    private Usuario getCurrentUser() {
        Optional<String> currentUser = this.auditorAware.getCurrentAuditor();

        return currentUser.map(s -> this.clienteService.findByNombreUsuario(s)).orElse(null);

    }
}