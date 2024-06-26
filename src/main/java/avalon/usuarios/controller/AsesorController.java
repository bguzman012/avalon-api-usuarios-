package avalon.usuarios.controller;

import avalon.usuarios.mapper.UsuarioMapper;
import avalon.usuarios.model.pojo.Asesor;
import avalon.usuarios.model.request.AsesorRequest;
import avalon.usuarios.model.request.PartiallyUpdateUsuario;
import avalon.usuarios.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AsesorController {

    @Autowired
    private AsesorService service;
    @Autowired
    private UsuarioMapper usuarioMapper;

    @PostMapping("/asesores")
    public ResponseEntity<Asesor> createAsesor(@RequestBody AsesorRequest request) {
        try {
            Asesor asesor = usuarioMapper.mapToUsuario(request, new Asesor());
            Asesor result = service.save(asesor);
            return result.getId() != null ? ResponseEntity.status(HttpStatus.CREATED).body(result) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/asesores")
    public ResponseEntity<List<Asesor>> getAsesores(@RequestParam(required = false) String estado) {
        List<Asesor> asesores;
        if (estado == null)
            asesores = service.findAll();
        else
            asesores = service.findAllByEstado(estado);

        if (!asesores.isEmpty()) {
            return ResponseEntity.ok(asesores);
        } else {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @GetMapping("/asesores/{asesorId}")
    public ResponseEntity<Asesor> getAsesor(@PathVariable Long asesorId) {
        Asesor asesor = service.findById(asesorId).orElseThrow(() -> new IllegalArgumentException("Asesor no encontrado"));

        if (asesor != null) {
            return ResponseEntity.ok(asesor);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/asesores/{asesorId}")
    public ResponseEntity<Asesor> partiallyUpdateAsesor(@RequestBody PartiallyUpdateUsuario request, @PathVariable Long asesorId) {
        Asesor asesor = service.findById(asesorId).orElseThrow(() -> new IllegalArgumentException("Asesor no encontrado"));

        Asesor result = service.partiallyUpdateUsuario(request, asesor);

        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/asesores/{asesorId}")
    public ResponseEntity<Asesor> updateAsesor(@PathVariable Long asesorId, @RequestBody AsesorRequest request) {
        Asesor asesor = service.findById(asesorId).orElseThrow(() -> new IllegalArgumentException("Asesor no encontrado"));
        Asesor asesorUpdate = usuarioMapper.mapToUsuario(request, asesor);

        service.save(asesorUpdate);
        return asesorUpdate != null ? ResponseEntity.ok(asesorUpdate) : ResponseEntity.badRequest().build();
    }

//    @GetMapping("/roles/{rolId}/usuarios")
//    public ResponseEntity<List<Usuario>> getUsuariosByRol(@PathVariable Long rolId, @RequestParam(required = false) String estado) {
//        List<Usuario> usuarios = service.getUsuariosByRolAndEstado(rolId, estado);
//
//        if (!usuarios.isEmpty()) {
//            return ResponseEntity.ok(usuarios);
//        } else {
//            return ResponseEntity.ok(Collections.emptyList());
//        }
//    }
//


}
