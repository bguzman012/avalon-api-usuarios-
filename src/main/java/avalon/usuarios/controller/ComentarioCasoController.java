package avalon.usuarios.controller;

import avalon.usuarios.model.pojo.Caso;
import avalon.usuarios.model.pojo.ComentarioCasos;
import avalon.usuarios.model.pojo.Usuario;
import avalon.usuarios.model.request.ComentarioCasoRequest;
import avalon.usuarios.service.CasoService;
import avalon.usuarios.service.ComentarioCasosService;
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
public class ComentarioCasoController {

    private final CasoService casoService;
    private final UsuariosService usuarioService;
    private final ComentarioCasosService comentarioCasosService;

    @Autowired
    public ComentarioCasoController(@Qualifier("usuariosServiceImpl") UsuariosService service, ComentarioCasosService comentarioCasosService, CasoService casoService) {
        this.casoService = casoService;
        this.usuarioService = service;
        this.comentarioCasosService = comentarioCasosService;
    }

    @PostMapping("/comentariosCasos")
    public ResponseEntity<ComentarioCasos> createComentario(@RequestBody ComentarioCasoRequest request) {
        try {
            ComentarioCasos comentarioCasos = this.mapToComentario(request, new ComentarioCasos());
            comentarioCasosService.saveComentario(comentarioCasos);
            return comentarioCasos.getId() != null ? ResponseEntity.status(HttpStatus.CREATED).body(comentarioCasos) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/casos/{casoId}/comentariosCasos")
    public ResponseEntity<List<ComentarioCasos>> getComentariosByCaso(@PathVariable Long casoId) {
        Caso caso = this.casoService.getCaso(casoId)
                .orElseThrow(() -> new IllegalArgumentException("Cita Medica no encontrada"));
        List<ComentarioCasos> comentarioCasosList = comentarioCasosService.getComentariosByCaso(caso);

        if (!comentarioCasosList.isEmpty()) {
            return ResponseEntity.ok(comentarioCasosList);
        } else {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @GetMapping("/comentariosCasos/{comentarioCasoId}")
    public ResponseEntity<ComentarioCasos> getComentario(@PathVariable Long comentarioCasoId) {
        ComentarioCasos comentarioCasos = comentarioCasosService.getComentario(comentarioCasoId).orElseThrow(() -> new IllegalArgumentException("Comentario no encontrado"));

        if (comentarioCasos != null) {
            return ResponseEntity.ok(comentarioCasos);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/comentariosCasos/{comentarioCasoId}")
    public ResponseEntity<ComentarioCasos> updateComentario(@PathVariable Long comentarioCasoId, @RequestBody ComentarioCasoRequest request) {
        ComentarioCasos comentarioCasos = comentarioCasosService.getComentario(comentarioCasoId).orElseThrow(() -> new IllegalArgumentException("Comentario no encontrado"));
        ComentarioCasos comentarioMapped = this.mapToComentario(request, comentarioCasos);
        comentarioCasosService.saveComentario(comentarioMapped);
        return comentarioMapped.getId() != null ? ResponseEntity.ok(comentarioCasos) : ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/comentariosCasos/{comentarioCasoId}")
    public ResponseEntity<Void> deleteComentario(@PathVariable Long comentarioCasoId) {
        comentarioCasosService.deleteComentario(comentarioCasoId);
        return ResponseEntity.noContent().build();
    }

    private ComentarioCasos mapToComentario(ComentarioCasoRequest request, ComentarioCasos comentario) {
        Caso caso = this.casoService.getCaso(request.getCasoId())
                .orElseThrow(() -> new IllegalArgumentException("Cita Medica no encontrada"));
        Usuario usuario = usuarioService.getUsuario(request.getUsuarioComentaId());

        comentario.setContenido(request.getContenido());
        comentario.setCaso(caso);
        comentario.setUsuarioComenta(usuario);
        comentario.setEstado(request.getEstado());

        return comentario;
    }
}
