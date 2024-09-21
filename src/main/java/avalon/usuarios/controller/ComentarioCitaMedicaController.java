package avalon.usuarios.controller;

import avalon.usuarios.config.AuditorAwareImpl;
import avalon.usuarios.model.pojo.*;
import avalon.usuarios.model.request.CitaMedicaRequest;
import avalon.usuarios.model.request.ComentarioCitaMedicaRequest;
import avalon.usuarios.model.request.ComentarioRequest;
import avalon.usuarios.model.request.PartiallyUpdateCitaMedicaRequest;
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
public class ComentarioCitaMedicaController {

    private final CitaMedicaService citaMedicaService;
    private final UsuariosService usuarioService;
    private final ComentarioCitasMedicasService comentarioCitasMedicasService;
    private String TOPICO = "IMAGEN_CITA_MEDICA_COMENTARIO";
    @Autowired
    private ImagenService imagenService;
    @Autowired
    private AuditorAwareImpl auditorAware;
    @Autowired
    private ClientesPolizaService clientesPolizaService;
    private final Long TIPO_NOTIFICACION_COMENTARIO_CITA = 10L;

    @Autowired
    public ComentarioCitaMedicaController(@Qualifier("usuariosServiceImpl") UsuariosService service, ComentarioCitasMedicasService comentarioCitasMedicasService, CitaMedicaService citaMedicaService) {
        this.citaMedicaService = citaMedicaService;
        this.usuarioService = service;
        this.comentarioCitasMedicasService = comentarioCitasMedicasService;
    }

    @PostMapping("/comentariosCitasMedicas")
    public ResponseEntity<ComentarioCitasMedicas> createComentario(@RequestPart("comentarioCitaMedica") ComentarioCitaMedicaRequest request,
                                                                   @RequestPart(value = "fotoComentarioCitaMedica", required = false) MultipartFile fotoComentarioCitaMedica) {
        try {
            CitaMedica citaMedica = this.citaMedicaService.getCitaMedica(request.getCitaMedicaId())
                    .orElseThrow(() -> new IllegalArgumentException("Cita Medica no encontrada"));

            List<ComentarioCitasMedicas> comentariosCitasMedicas = this.comentarioCitasMedicasService.getComentariosByCitaMedica(citaMedica);

            // Si no tiene comentarios, cambia el estado de la cita medica a G --> Gestionando
            if (comentariosCitasMedicas.isEmpty()) {
                PartiallyUpdateCitaMedicaRequest partiallyUpdateCitaMedicaRequest = new PartiallyUpdateCitaMedicaRequest();
                partiallyUpdateCitaMedicaRequest.setEstado("G");
                this.citaMedicaService.partiallyUpdateCitaMedica(partiallyUpdateCitaMedicaRequest, citaMedica.getId());
            }

            ComentarioCitasMedicas comentarioCitasMedicas = this.mapToComentario(request, new ComentarioCitasMedicas());

            if (fotoComentarioCitaMedica != null && !fotoComentarioCitaMedica.isEmpty()) {
                Imagen imagen = new Imagen(fotoComentarioCitaMedica.getBytes(), this.TOPICO, request.getNombreDocumento());
                this.imagenService.saveImagen(imagen);
                comentarioCitasMedicas.setImagenId(imagen.getId());
            }

            comentarioCitasMedicasService.saveComentario(comentarioCitasMedicas);

            Optional<String> currentUser = this.auditorAware.getCurrentAuditor();

            if (currentUser.isEmpty())
                return ResponseEntity.notFound().build();

            Usuario usuario = this.usuarioService.findByNombreUsuario(currentUser.get());
            this.clientesPolizaService.enviarNotificacionesMiembrosClientePolizas(comentarioCitasMedicas.getCitaMedica().getClientePoliza(), "Comentario creado", "Se ha agregado un comentario en una cita médica", usuario, TIPO_NOTIFICACION_COMENTARIO_CITA);
            return comentarioCitasMedicas.getId() != null ? ResponseEntity.status(HttpStatus.CREATED).body(comentarioCitasMedicas) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/citasMedicas/{citaMedicaId}/comentariosCitasMedicas")
    public ResponseEntity<List<ComentarioCitasMedicas>> getComentariosByCitaMedica(@PathVariable Long citaMedicaId) {
        CitaMedica citaMedica = this.citaMedicaService.getCitaMedica(citaMedicaId)
                .orElseThrow(() -> new IllegalArgumentException("Cita Medica no encontrada"));
        List<ComentarioCitasMedicas> comentarioCitasMedicasList = comentarioCitasMedicasService.getComentariosByCitaMedica(citaMedica);

        if (!comentarioCitasMedicasList.isEmpty()) {
            return ResponseEntity.ok(comentarioCitasMedicasList);
        } else {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @GetMapping("/comentariosCitasMedicas/{comentarioCitaMedicaId}")
    public ResponseEntity<ComentarioCitasMedicas> getComentario(@PathVariable Long comentarioCitaMedicaId) {
        ComentarioCitasMedicas comentarioCitasMedicas = comentarioCitasMedicasService.getComentario(comentarioCitaMedicaId).orElseThrow(() -> new IllegalArgumentException("Comentario no encontrado"));

        if (comentarioCitasMedicas != null) {
            return ResponseEntity.ok(comentarioCitasMedicas);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/comentariosCitasMedicas/{comentarioCitaMedicaId}")
    public ResponseEntity<ComentarioCitasMedicas> updateComentario(@PathVariable Long comentarioCitaMedicaId,
                                                                   @RequestPart("comentarioCitaMedica") ComentarioCitaMedicaRequest request,
                                                                   @RequestPart(value = "fotoComentarioCitaMedica", required = false) MultipartFile fotoComentarioCitaMedica) {
        try {

            ComentarioCitasMedicas comentarioCitasMedicas = comentarioCitasMedicasService.getComentario(comentarioCitaMedicaId).orElseThrow(() -> new IllegalArgumentException("Comentario no encontrado"));
            ComentarioCitasMedicas comentarioMapped = this.mapToComentario(request, comentarioCitasMedicas);

            if (comentarioMapped.getImagenId() != null) {
                this.imagenService.deleteImagen(comentarioCitasMedicas.getImagenId());
                comentarioMapped.setImagenId(null);
            }

            if (fotoComentarioCitaMedica != null && !fotoComentarioCitaMedica.isEmpty()) {
                Imagen imagen = new Imagen(fotoComentarioCitaMedica.getBytes(), this.TOPICO, request.getNombreDocumento());
                this.imagenService.saveImagen(imagen);
                comentarioMapped.setImagenId(imagen.getId());
            }

            comentarioCitasMedicasService.saveComentario(comentarioMapped);
            return comentarioMapped.getId() != null ? ResponseEntity.ok(comentarioCitasMedicas) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/comentariosCitasMedicas/{comentarioCitaMedicaId}")
    public ResponseEntity<Void> deleteComentario(@PathVariable Long comentarioCitaMedicaId) {
        comentarioCitasMedicasService.deleteComentario(comentarioCitaMedicaId);
        return ResponseEntity.noContent().build();
    }

    private ComentarioCitasMedicas mapToComentario(ComentarioCitaMedicaRequest request, ComentarioCitasMedicas comentario) {
        CitaMedica citaMedica = this.citaMedicaService.getCitaMedica(request.getCitaMedicaId())
                .orElseThrow(() -> new IllegalArgumentException("Cita Medica no encontrada"));
        Usuario usuario = usuarioService.getUsuario(request.getUsuarioComentaId());

        comentario.setContenido(request.getContenido());
        comentario.setCitaMedica(citaMedica);
        comentario.setUsuarioComenta(usuario);
        comentario.setEstado(request.getEstado());

        return comentario;
    }
}
