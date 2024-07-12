package avalon.usuarios.controller;

import avalon.usuarios.model.pojo.*;
import avalon.usuarios.model.request.ComentarioCitaMedicaRequest;
import avalon.usuarios.model.request.ComentarioRequest;
import avalon.usuarios.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@Slf4j
public class ComentarioCitaMedicaController {

    private final CitaMedicaService citaMedicaService;
    private final UsuariosService usuarioService;
    private final ComentarioCitasMedicasService comentarioCitasMedicasService;

    @Autowired
    public ComentarioCitaMedicaController(@Qualifier("usuariosServiceImpl") UsuariosService service, ComentarioCitasMedicasService comentarioCitasMedicasService, CitaMedicaService citaMedicaService) {
        this.citaMedicaService = citaMedicaService;
        this.usuarioService = service;
        this.comentarioCitasMedicasService = comentarioCitasMedicasService;
    }

    @PostMapping("/comentariosCitasMedicas")
    public ResponseEntity<ComentarioCitasMedicas> createComentario(@RequestBody ComentarioCitaMedicaRequest request) {
        try {
            ComentarioCitasMedicas comentarioCitasMedicas = this.mapToComentario(request, new ComentarioCitasMedicas());
            comentarioCitasMedicasService.saveComentario(comentarioCitasMedicas);
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
    public ResponseEntity<ComentarioCitasMedicas> updateComentario(@PathVariable Long comentarioCitaMedicaId, @RequestBody ComentarioCitaMedicaRequest request) {
        ComentarioCitasMedicas comentarioCitasMedicas = comentarioCitasMedicasService.getComentario(comentarioCitaMedicaId).orElseThrow(() -> new IllegalArgumentException("Comentario no encontrado"));
        ComentarioCitasMedicas comentarioMapped = this.mapToComentario(request, comentarioCitasMedicas);
        comentarioCitasMedicasService.saveComentario(comentarioMapped);
        return comentarioMapped.getId() != null ? ResponseEntity.ok(comentarioCitasMedicas) : ResponseEntity.badRequest().build();
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
