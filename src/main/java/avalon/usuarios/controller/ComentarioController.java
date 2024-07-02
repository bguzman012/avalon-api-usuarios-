package avalon.usuarios.controller;

import avalon.usuarios.model.pojo.Comentario;
import avalon.usuarios.model.pojo.Reclamacion;
import avalon.usuarios.model.pojo.Usuario;
import avalon.usuarios.model.request.ComentarioRequest;
import avalon.usuarios.service.ClientesPolizaService;
import avalon.usuarios.service.ComentarioService;
import avalon.usuarios.service.ReclamacionService;
import avalon.usuarios.service.UsuariosService;
import lombok.RequiredArgsConstructor;
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
public class ComentarioController {

    private final ReclamacionService reclamacionService;
    private final UsuariosService usuarioService;
    private final ComentarioService comentarioService;

    @Autowired
    public ComentarioController(@Qualifier("usuariosServiceImpl") UsuariosService service, ComentarioService comentarioService, ReclamacionService reclamacionService) {
        this.reclamacionService = reclamacionService;
        this.usuarioService = service;
        this.comentarioService = comentarioService;
    }

    @PostMapping("/comentarios")
    public ResponseEntity<Comentario> createComentario(@RequestBody ComentarioRequest request) {
        try {
            Comentario comentario = this.mapToComentario(request, new Comentario());
            comentarioService.saveComentario(comentario);
            return comentario.getId() != null ? ResponseEntity.status(HttpStatus.CREATED).body(comentario) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/reclamaciones/{reclamacionId}/comentarios")
    public ResponseEntity<List<Comentario>> getComentariosByReclamacion(@PathVariable Long reclamacionId) {
        Reclamacion reclamacion = reclamacionService.findByIdWithoutImage(reclamacionId).orElseThrow(() -> new IllegalArgumentException("Reclamación no encontrada"));
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
    public ResponseEntity<Comentario> updateComentario(@PathVariable Long comentarioId, @RequestBody ComentarioRequest request) {
        Comentario comentario = comentarioService.getComentario(comentarioId).orElseThrow(() -> new IllegalArgumentException("Comentario no encontrado"));
        Comentario comentarioMapped = this.mapToComentario(request, comentario);
        comentarioService.saveComentario(comentarioMapped);
        return comentarioMapped.getId() != null ? ResponseEntity.ok(comentario) : ResponseEntity.badRequest().build();
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
