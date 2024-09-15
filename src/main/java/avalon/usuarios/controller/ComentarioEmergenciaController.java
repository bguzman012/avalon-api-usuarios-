package avalon.usuarios.controller;

import avalon.usuarios.config.AuditorAwareImpl;
import avalon.usuarios.model.pojo.*;
import avalon.usuarios.model.request.ComentarioEmergenciaRequest;
import avalon.usuarios.model.request.PartiallyUpdateEmergenciasRequest;
import avalon.usuarios.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class ComentarioEmergenciaController {

    private final EmergenciaService emergenciaService;
    private final UsuariosService usuarioService;
    private final ComentarioEmergenciaService comentarioEmergenciaService;
    private String TOPICO = "IMAGEN_CITA_MEDICA_EMERGENCIA";
    @Autowired
    private ImagenService imagenService;
    @Autowired
    private AuditorAwareImpl auditorAware;
    @Autowired
    private ClientesPolizaService clientesPolizaService;

    @Autowired
    public ComentarioEmergenciaController(@Qualifier("usuariosServiceImpl") UsuariosService service, ComentarioEmergenciaService comentarioEmergenciaService, EmergenciaService emergenciaService) {
        this.emergenciaService = emergenciaService;
        this.usuarioService = service;
        this.comentarioEmergenciaService = comentarioEmergenciaService;
    }

    @PostMapping("/comentariosEmergencias")
    public ResponseEntity<ComentarioEmergencia> createComentario(@RequestPart("comentarioEmergencia") ComentarioEmergenciaRequest request,
                                                                 @RequestPart(value = "fotoComentarioEmergencia", required = false) MultipartFile fotoComentarioEmergencia) {
        try {
            Emergencia emergencia = this.emergenciaService.getEmergencia(request.getEmergenciaId())
                    .orElseThrow(() -> new IllegalArgumentException("Cita Medica no encontrada"));

            List<ComentarioEmergencia> comentariosEmergencias = this.comentarioEmergenciaService.getComentariosByEmergencia(emergencia);

            // Si no tiene comentarios, cambia el estado de la cita medica a G --> Gestionando
            if (comentariosEmergencias.isEmpty()) {
                PartiallyUpdateEmergenciasRequest partiallyUpdateEmergenciaRequest = new PartiallyUpdateEmergenciasRequest();
                partiallyUpdateEmergenciaRequest.setEstado("G");
                this.emergenciaService.partiallyUpdateEmergencia(partiallyUpdateEmergenciaRequest, emergencia.getId());
            }

            ComentarioEmergencia comentarioEmergencia = this.mapToComentario(request, new ComentarioEmergencia());

            if (fotoComentarioEmergencia != null && !fotoComentarioEmergencia.isEmpty()) {
                Imagen imagen = new Imagen(fotoComentarioEmergencia.getBytes(), this.TOPICO, request.getNombreDocumento());
                this.imagenService.saveImagen(imagen);
                comentarioEmergencia.setImagenId(imagen.getId());
            }

            comentarioEmergenciaService.saveComentario(comentarioEmergencia);

            Optional<String> currentUser = this.auditorAware.getCurrentAuditor();

            if (currentUser.isEmpty())
                return ResponseEntity.notFound().build();

            Usuario usuario = this.usuarioService.findByNombreUsuario(currentUser.get());
            this.clientesPolizaService.enviarNotificacionesMiembrosClientePolizas(comentarioEmergencia.getEmergencia().getClientePoliza(), "Comentario creado", "Se ha agregado un comentario en una emergencia", usuario);
            return comentarioEmergencia.getId() != null ? ResponseEntity.status(HttpStatus.CREATED).body(comentarioEmergencia) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/emergencias/{emergenciaId}/comentariosEmergencias")
    public ResponseEntity<List<ComentarioEmergencia>> getComentariosByEmergencia(@PathVariable Long emergenciaId) {
        Emergencia emergencia = this.emergenciaService.getEmergencia(emergenciaId)
                .orElseThrow(() -> new IllegalArgumentException("Cita Medica no encontrada"));
        List<ComentarioEmergencia> comentarioEmergenciaList = comentarioEmergenciaService.getComentariosByEmergencia(emergencia);

        if (!comentarioEmergenciaList.isEmpty()) {
            return ResponseEntity.ok(comentarioEmergenciaList);
        } else {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @GetMapping("/comentariosEmergencias/{comentarioEmergenciaId}")
    public ResponseEntity<ComentarioEmergencia> getComentario(@PathVariable Long comentarioEmergenciaId) {
        ComentarioEmergencia comentarioEmergencia = comentarioEmergenciaService.getComentario(comentarioEmergenciaId).orElseThrow(() -> new IllegalArgumentException("Comentario no encontrado"));

        if (comentarioEmergencia != null) {
            return ResponseEntity.ok(comentarioEmergencia);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/comentariosEmergencias/{comentarioEmergenciaId}")
    public ResponseEntity<ComentarioEmergencia> updateComentario(@PathVariable Long comentarioEmergenciaId,
                                                                 @RequestPart("comentarioEmergencia") ComentarioEmergenciaRequest request,
                                                                 @RequestPart(value = "fotoComentarioEmergencia", required = false) MultipartFile fotoComentarioEmergencia) {
        try {

            ComentarioEmergencia comentarioEmergencia = comentarioEmergenciaService.getComentario(comentarioEmergenciaId).orElseThrow(() -> new IllegalArgumentException("Comentario no encontrado"));
            ComentarioEmergencia comentarioMapped = this.mapToComentario(request, comentarioEmergencia);

            if (comentarioMapped.getImagenId() != null) {
                this.imagenService.deleteImagen(comentarioEmergencia.getImagenId());
                comentarioMapped.setImagenId(null);
            }

            if (fotoComentarioEmergencia != null && !fotoComentarioEmergencia.isEmpty()) {
                Imagen imagen = new Imagen(fotoComentarioEmergencia.getBytes(), this.TOPICO, request.getNombreDocumento());
                this.imagenService.saveImagen(imagen);
                comentarioMapped.setImagenId(imagen.getId());
            }

            comentarioEmergenciaService.saveComentario(comentarioMapped);
            return comentarioMapped.getId() != null ? ResponseEntity.ok(comentarioEmergencia) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/comentariosEmergencias/{comentarioEmergenciaId}")
    public ResponseEntity<Void> deleteComentario(@PathVariable Long comentarioEmergenciaId) {
        comentarioEmergenciaService.deleteComentario(comentarioEmergenciaId);
        return ResponseEntity.noContent().build();
    }

    private ComentarioEmergencia mapToComentario(ComentarioEmergenciaRequest request, ComentarioEmergencia comentario) {
        Emergencia emergencia = this.emergenciaService.getEmergencia(request.getEmergenciaId())
                .orElseThrow(() -> new IllegalArgumentException("Cita Medica no encontrada"));
        Usuario usuario = usuarioService.getUsuario(request.getUsuarioComentaId());

        comentario.setContenido(request.getContenido());
        comentario.setEmergencia(emergencia);
        comentario.setUsuarioComenta(usuario);
        comentario.setEstado(request.getEstado());

        return comentario;
    }
}
