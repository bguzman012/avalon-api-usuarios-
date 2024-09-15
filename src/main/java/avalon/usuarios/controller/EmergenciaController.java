package avalon.usuarios.controller;

import avalon.usuarios.config.AuditorAwareImpl;
import avalon.usuarios.model.pojo.*;
import avalon.usuarios.model.request.*;
import avalon.usuarios.model.response.PaginatedResponse;
import avalon.usuarios.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EmergenciaController {

    private final EmergenciaService service;
    private final UsuariosService usuariosService;
    @Autowired
    private ClientesPolizaService clientesPolizaService;
    @Autowired
    private ImagenService imagenService;
    @Autowired
    private MedicoCentroMedicoAseguradoraService medicoCentroMedicoAseguradoraService;
    @Autowired
    private ComentarioEmergenciaService comentarioEmergenciaService;
    @Autowired
    private CasoService casoService;
    @Autowired
    private PaisService paisService;
    @Autowired
    private EstadosService estadosService;
    @Autowired
    private AuditorAwareImpl auditorAware;
    private String TOPICO = "IMAGEN_EMERGENCIA";

    @Autowired
    public EmergenciaController(@Qualifier("usuariosServiceImpl") UsuariosService usuariosService, EmergenciaService emergenciaService) {
        this.service = emergenciaService;
        this.usuariosService = usuariosService;
    }

    @PostMapping("/emergencias")
    public ResponseEntity<Emergencia> createEmergencia(@RequestPart("emergencia") EmergenciaRequest request,
                                                       @RequestPart(value = "fotoEmergencia", required = false) MultipartFile fotoEmergencia) {
        try {
            request.setEstado("N");
            Emergencia emergencia = this.mapToEmergencia(request, new Emergencia(), new Direccion());

            if (fotoEmergencia != null && !fotoEmergencia.isEmpty()) {
                Imagen imagen = new Imagen(fotoEmergencia.getBytes(), this.TOPICO, request.getNombreDocumento());
                this.imagenService.saveImagen(imagen);
                emergencia.setImagenId(imagen.getId());
            }

            service.saveEmergencia(emergencia);

            Optional<String> currentUser = this.auditorAware.getCurrentAuditor();

            if (currentUser.isEmpty())
                return ResponseEntity.notFound().build();

            Usuario usuario = this.usuariosService.findByNombreUsuario(currentUser.get());
            this.clientesPolizaService.enviarNotificacionesMiembrosClientePolizas(emergencia.getClientePoliza(), "Emergencia creada", "Se ha creado una emergencia", usuario);
            return emergencia.getId() != null ? ResponseEntity.status(HttpStatus.CREATED).body(emergencia) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/emergencias")
    public ResponseEntity<PaginatedResponse<Emergencia>> getEmergencias(@RequestParam(required = false) String estado,
                                                                        @RequestParam(required = false) String clientePolizaId,
                                                                        @RequestParam(required = false) String casoId,
                                                                        @RequestParam(required = false) String busqueda,
                                                                        @RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "10") int size,
                                                                        @RequestParam(defaultValue = "createdDate") String sortField,
                                                                        @RequestParam(defaultValue = "desc") String sortOrder) {
        Optional<String> currentUser = this.auditorAware.getCurrentAuditor();

        if (currentUser.isEmpty())
            return ResponseEntity.notFound().build();

        Usuario usuario = this.usuariosService.findByNombreUsuario(currentUser.get());

        ClientePoliza clientePoliza = null;
        Caso caso = null;

        if (clientePolizaId != null && !clientePolizaId.isBlank()) {
            clientePoliza = clientesPolizaService.getClientePoliza(Long.valueOf(clientePolizaId))
                    .orElseThrow(() -> new IllegalArgumentException("Cliente Poliza no encontrada"));
        }

        if (casoId != null && !casoId.isBlank()) {
            caso = casoService.getCaso(Long.valueOf(casoId))
                    .orElseThrow(() -> new IllegalArgumentException("Caso no encontrada"));
        }

        Sort sort = sortOrder.equalsIgnoreCase("desc") ? Sort.by(sortField).descending() : Sort.by(sortField).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Emergencia> emergenciaPage = service.searchEmergencias(busqueda, estado, pageable, clientePoliza, caso, usuario);

        List<Emergencia> emergencias = emergenciaPage.getContent();
        long totalRecords = emergenciaPage.getTotalElements();

        PaginatedResponse<Emergencia> response = new PaginatedResponse<>(emergencias, totalRecords);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/emergencias/{emergenciaId}")
    public ResponseEntity<Emergencia> getEmergenciaById(@PathVariable Long emergenciaId) {
        try {
            return service.getEmergencia(emergenciaId).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/emergencias/{emergenciaId}")
    public ResponseEntity<Emergencia> partiallyUpdateEmergencia(@RequestBody PartiallyUpdateEmergenciasRequest request, @PathVariable Long emergenciaId) {
        try {
            Emergencia emergencia = service.partiallyUpdateEmergencia(request, emergenciaId);

            if (request.getEstado().equals("C") && request.getComentarioEmergenciaRequest() != null && emergencia != null) {
                this.crearComentarioCierreCitasMedicas(request.getComentarioEmergenciaRequest(), emergencia);

                Optional<String> currentUser = this.auditorAware.getCurrentAuditor();

                if (currentUser.isEmpty())
                    return ResponseEntity.notFound().build();

                Usuario usuario = this.usuariosService.findByNombreUsuario(currentUser.get());
                this.clientesPolizaService.enviarNotificacionesMiembrosClientePolizas(emergencia.getClientePoliza(), "Emergencia cerrada", "Se ha cerrado una emergencia", usuario);
            }
            return emergencia == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(emergencia);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/emergencias/{emergenciaId}")
    public ResponseEntity<Emergencia> updateEmergencia(@PathVariable Long emergenciaId,
                                                       @RequestPart("emergencia") EmergenciaRequest request,
                                                       @RequestPart(value = "fotoEmergencia", required = false) MultipartFile fotoEmergencia) {
        try {
            Emergencia emergencia = service.getEmergencia(emergenciaId).orElseThrow(() -> new IllegalArgumentException("Emergencia no encontrada"));
            Emergencia emergenciaMapped = this.mapToEmergencia(request, emergencia, emergencia.getDireccion());

            if (emergencia.getImagenId() != null) {
                this.imagenService.deleteImagen(emergencia.getImagenId());
                emergencia.setImagenId(null);
            }

            if (fotoEmergencia != null && !fotoEmergencia.isEmpty()) {
                Imagen imagen = new Imagen(fotoEmergencia.getBytes(), this.TOPICO, request.getNombreDocumento());
                this.imagenService.saveImagen(imagen);
                emergenciaMapped.setImagenId(imagen.getId());
            }

            service.saveEmergencia(emergenciaMapped);
            return emergencia.getId() != null ? ResponseEntity.ok(emergencia) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/emergencias/{emergenciaId}")
    public ResponseEntity<Void> deleteEmergencia(@PathVariable Long emergenciaId) {
        try {
            service.deleteEmergencia(emergenciaId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private Emergencia mapToEmergencia(EmergenciaRequest request, Emergencia emergencia, Direccion direccion) {
        ClientePoliza clientePoliza = clientesPolizaService.getClientePoliza(request.getClientePolizaId()).orElseThrow(() -> new IllegalArgumentException("Cliente Poliza no encontrada"));
        MedicoCentroMedicoAseguradora medicoCentroMedicoAseguradora = null;
        if (request.getMedicoCentroMedicoAseguradoraId() != null) {
            medicoCentroMedicoAseguradora = medicoCentroMedicoAseguradoraService.getMedicoCentroMedicoAseguradora(
                    request.getMedicoCentroMedicoAseguradoraId()).orElseThrow(() -> new IllegalArgumentException("Centro Médico no encontrado"));
        }

        Pais pais = paisService.findById(request.getDireccion().getPaisId()).orElseThrow(() -> new IllegalArgumentException("País no encontrado"));
        Estado estado = estadosService.findById(request.getDireccion().getEstadoId()).orElseThrow(() -> new IllegalArgumentException("Estado no encontrado"));
        Caso caso = casoService.getCaso(request.getCasoId()).orElseThrow(() -> new IllegalArgumentException("Caso no encontrado"));


        emergencia.setEstado(request.getEstado());
        emergencia.setClientePoliza(clientePoliza);
        emergencia.setMedicoCentroMedicoAseguradora(medicoCentroMedicoAseguradora);
        emergencia.setDiagnostico(request.getDiagnostico());
        emergencia.setSintomas(request.getSintomas());
        emergencia.setCaso(caso);


        direccion.setDireccionUno(request.getDireccion().getDireccionUno());
        direccion.setDireccionDos(request.getDireccion().getDireccionDos());
        direccion.setCodigoPostal(request.getDireccion().getCodigoPostal());
        direccion.setPais(pais);
        direccion.setState(estado);
        direccion.setCiudad(request.getDireccion().getCiudad());

        emergencia.setDireccion(direccion);
        return emergencia;
    }

    private void crearComentarioCierreCitasMedicas(ComentarioEmergenciaRequest comentarioEmergenciaRequest, Emergencia emergencia) {
        Usuario usuario = this.usuariosService.getUsuario(comentarioEmergenciaRequest.getUsuarioComentaId());

        ComentarioEmergencia comentarioEmergencia = new ComentarioEmergencia();
        comentarioEmergencia.setEstado(comentarioEmergenciaRequest.getEstado());
        comentarioEmergencia.setUsuarioComenta(usuario);
        comentarioEmergencia.setEmergencia(emergencia);

        if (comentarioEmergenciaRequest.getContenido().isEmpty()) {
            String textoCierre = "Se ha cerrado por el " + usuario.getRol().getNombre() + " " +
                    usuario.getNombres() + " " + usuario.getApellidos() + " (" + usuario.getNombreUsuario() + ")";
            comentarioEmergencia.setContenido(textoCierre);
        } else
            comentarioEmergencia.setContenido(comentarioEmergenciaRequest.getContenido());

        comentarioEmergenciaService.saveComentario(comentarioEmergencia);
    }
}
