package avalon.usuarios.controller;

import avalon.usuarios.model.pojo.Emergencia;
import avalon.usuarios.model.pojo.ComentarioEmergencia;
import avalon.usuarios.model.pojo.Usuario;
import avalon.usuarios.model.request.ComentarioEmergenciaRequest;
import avalon.usuarios.service.EmergenciaService;
import avalon.usuarios.service.ComentarioEmergenciaService;
import avalon.usuarios.service.UsuariosService;
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
public class ComentarioEmergenciaController {

    private final EmergenciaService emergenciaService;
    private final UsuariosService usuarioService;
    private final ComentarioEmergenciaService comentarioEmergenciaService;

    @Autowired
    public ComentarioEmergenciaController(@Qualifier("usuariosServiceImpl") UsuariosService service, ComentarioEmergenciaService comentarioEmergenciaService, EmergenciaService emergenciaService) {
        this.emergenciaService = emergenciaService;
        this.usuarioService = service;
        this.comentarioEmergenciaService = comentarioEmergenciaService;
    }

    @PostMapping("/comentariosEmergencias")
    public ResponseEntity<ComentarioEmergencia> createComentario(@RequestBody ComentarioEmergenciaRequest request) {
        try {
            ComentarioEmergencia comentarioEmergencia = this.mapToComentario(request, new ComentarioEmergencia());
            comentarioEmergenciaService.saveComentario(comentarioEmergencia);
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
    public ResponseEntity<ComentarioEmergencia> updateComentario(@PathVariable Long comentarioEmergenciaId, @RequestBody ComentarioEmergenciaRequest request) {
        ComentarioEmergencia comentarioEmergencia = comentarioEmergenciaService.getComentario(comentarioEmergenciaId).orElseThrow(() -> new IllegalArgumentException("Comentario no encontrado"));
        ComentarioEmergencia comentarioMapped = this.mapToComentario(request, comentarioEmergencia);
        comentarioEmergenciaService.saveComentario(comentarioMapped);
        return comentarioMapped.getId() != null ? ResponseEntity.ok(comentarioEmergencia) : ResponseEntity.badRequest().build();
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
