package avalon.usuarios.controller;

import avalon.usuarios.config.AuditorAwareImpl;
import avalon.usuarios.model.pojo.*;
import avalon.usuarios.model.request.ComentarioRequest;
import avalon.usuarios.model.request.PartiallyUpdateCitaMedicaRequest;
import avalon.usuarios.model.request.PartiallyUpdateReclamacionRequest;
import avalon.usuarios.service.*;
import lombok.RequiredArgsConstructor;
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
public class ComentarioController {

    private final ReclamacionService reclamacionService;
    private final UsuariosService usuarioService;
    private final ComentarioService comentarioService;
    private String TOPICO = "IMAGEN_RECLAMACION_COMENTARIO";
    @Autowired
    private ImagenService imagenService;
    @Autowired
    private AuditorAwareImpl auditorAware;
    @Autowired
    private ClientesPolizaService clientesPolizaService;

    @Autowired
    public ComentarioController(@Qualifier("usuariosServiceImpl") UsuariosService service, ComentarioService comentarioService, ReclamacionService reclamacionService) {
        this.reclamacionService = reclamacionService;
        this.usuarioService = service;
        this.comentarioService = comentarioService;
    }

    @PostMapping("/comentarios")
    public ResponseEntity<Comentario> createComentario(@RequestPart("comentarioReclamacion") ComentarioRequest request,
                                                       @RequestPart(value = "fotoComentarioReclamacion", required = false) MultipartFile fotoComentarioReclamacion) {
        try {
            Reclamacion reclamacion = this.reclamacionService.getReclamacion(request.getReclamacionId())
                    .orElseThrow(() -> new IllegalArgumentException("Reclamación no encontrada"));

            List<Comentario> comentariosReclamos = this.comentarioService.getComentariosByReclamacion(reclamacion);

            // Si no tiene comentarios, cambia el estado de la cita medica a G --> Gestionando
            if (comentariosReclamos.isEmpty()) {
                PartiallyUpdateReclamacionRequest partiallyUpdateReclamacionRequest = new PartiallyUpdateReclamacionRequest();
                partiallyUpdateReclamacionRequest.setEstado("G");
                this.reclamacionService.partiallyUpdateReclamacion(partiallyUpdateReclamacionRequest, reclamacion.getId());
            }

            Comentario comentario = this.mapToComentario(request, new Comentario());

            if (fotoComentarioReclamacion != null && !fotoComentarioReclamacion.isEmpty()) {
                Imagen imagen = new Imagen(fotoComentarioReclamacion.getBytes(), this.TOPICO, request.getNombreDocumento());
                this.imagenService.saveImagen(imagen);
                comentario.setImagenId(imagen.getId());
            }

            comentarioService.saveComentario(comentario);

            Optional<String> currentUser = this.auditorAware.getCurrentAuditor();

            if (currentUser.isEmpty())
                return ResponseEntity.notFound().build();

            Usuario usuario = this.usuarioService.findByNombreUsuario(currentUser.get());
            this.clientesPolizaService.enviarNotificacionesMiembrosClientePolizas(comentario.getReclamacion().getClientePoliza(), "Comentario creado", "Se ha agregado un comentario en un reembolso", usuario);
            return comentario.getId() != null ? ResponseEntity.status(HttpStatus.CREATED).body(comentario) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/reclamaciones/{reclamacionId}/comentarios")
    public ResponseEntity<List<Comentario>> getComentariosByReclamacion(@PathVariable Long reclamacionId) {
        Reclamacion reclamacion = reclamacionService.getReclamacion(reclamacionId).orElseThrow(() -> new IllegalArgumentException("Reclamación no encontrada"));
        List<Comentario> comentarios = comentarioService.getComentariosByReclamacion(reclamacion);

        if (!comentarios.isEmpty()) {
            return ResponseEntity.ok(comentarios);
        } else {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @GetMapping("/comentarios/{comentarioId}")
    public ResponseEntity<Comentario> getComentario(@PathVariable Long comentarioId) {
        Comentario comentario = comentarioService.getComentario(comentarioId).orElseThrow(() -> new IllegalArgumentException("Comentario no encontrado"));

        if (comentario != null) {
            return ResponseEntity.ok(comentario);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/comentarios/{comentarioId}")
    public ResponseEntity<Comentario> updateComentario(@PathVariable Long comentarioId,
                                                       @RequestPart("comentarioReclamacion") ComentarioRequest request,
                                                       @RequestPart(value = "fotoComentarioReclamacion", required = false) MultipartFile fotoComentarioReclamacion) {
        try {

            Comentario comentario = comentarioService.getComentario(comentarioId).orElseThrow(() -> new IllegalArgumentException("Comentario no encontrado"));
            Comentario comentarioMapped = this.mapToComentario(request, comentario);

            if (comentarioMapped.getImagenId() != null) {
                this.imagenService.deleteImagen(comentario.getImagenId());
                comentarioMapped.setImagenId(null);
            }

            if (fotoComentarioReclamacion != null && !fotoComentarioReclamacion.isEmpty()) {
                Imagen imagen = new Imagen(fotoComentarioReclamacion.getBytes(), this.TOPICO, request.getNombreDocumento());
                this.imagenService.saveImagen(imagen);
                comentarioMapped.setImagenId(imagen.getId());
            }

            comentarioService.saveComentario(comentarioMapped);
            return comentarioMapped.getId() != null ? ResponseEntity.ok(comentario) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/comentarios/{comentarioId}")
    public ResponseEntity<Void> deleteComentario(@PathVariable Long comentarioId) {
        comentarioService.deleteComentario(comentarioId);
        return ResponseEntity.noContent().build();
    }

    private Comentario mapToComentario(ComentarioRequest request, Comentario comentario) {
        Reclamacion reclamacion = this.reclamacionService.getReclamacion(request.getReclamacionId())
                .orElseThrow(() -> new IllegalArgumentException("Reclamacion no encontrada"));
        Usuario usuario = usuarioService.getUsuario(request.getUsuarioComentaId());

        comentario.setContenido(request.getContenido());
        comentario.setReclamacion(reclamacion);
        comentario.setUsuarioComenta(usuario);
        comentario.setEstado(request.getEstado());

        return comentario;
    }

}
