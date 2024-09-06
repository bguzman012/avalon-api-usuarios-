package avalon.usuarios.controller;

import avalon.usuarios.mapper.UsuarioMapper;
import avalon.usuarios.model.pojo.Asesor;
import avalon.usuarios.model.pojo.Cliente;
import avalon.usuarios.model.request.AsesorRequest;
import avalon.usuarios.model.request.PartiallyUpdateUsuario;
import avalon.usuarios.model.response.PaginatedResponse;
import avalon.usuarios.service.*;
import avalon.usuarios.util.ExceptionHandlerUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
    public ResponseEntity<?> createAsesor(@RequestBody AsesorRequest request) {
        try {
            Asesor asesor = usuarioMapper.mapToUsuario(request, new Asesor());
            Asesor result = service.save(asesor);
            return result.getId() != null ? ResponseEntity.status(HttpStatus.CREATED).body(result) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ExceptionHandlerUtil.userHandleException(e);
//            String err = e.getMessage();
//            if (err.contains("uk_correo_electronico"))
//                return ResponseEntity.status(HttpStatus.CONFLICT)
//                        .body("Ocurrió un error al persistir la información, el correo electrónico ya le pertenece a otro usuario.");
//
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body("Ocurrió un error al persistir la información.");
        }
    }

    @GetMapping("/asesores")
    public ResponseEntity<PaginatedResponse<Asesor>> getAsesores(@RequestParam(required = false) String estado,
                                                                 @RequestParam(required = false) String busqueda,
                                                                 @RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size,
                                                                 @RequestParam(defaultValue = "createdDate") String sortField,
                                                                 @RequestParam(defaultValue = "desc") String sortOrder
    ) {

        Sort sort = sortOrder.equalsIgnoreCase("desc") ? Sort.by(sortField).descending() : Sort.by(sortField).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Asesor> asesorPage = service.searchAsesores(estado, busqueda, pageable);

        List<Asesor> asesores = asesorPage.getContent();
        long totalRecords = asesorPage.getTotalElements();

        PaginatedResponse<Asesor> response = new PaginatedResponse<>(asesores, totalRecords);
        return ResponseEntity.ok(response);
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
    public ResponseEntity<Asesor> partiallyUpdateAsesor(@RequestBody PartiallyUpdateUsuario request, @PathVariable Long asesorId) throws MessagingException, IOException {
        Asesor asesor = service.findById(asesorId).orElseThrow(() -> new IllegalArgumentException("Asesor no encontrado"));

        Asesor result = service.partiallyUpdateUsuario(request, asesor);

        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/asesores/{asesorId}")
    public ResponseEntity<?> updateAsesor(@PathVariable Long asesorId, @RequestBody AsesorRequest request) throws MessagingException, IOException {
        try {
            Asesor asesor = service.findById(asesorId).orElseThrow(() -> new IllegalArgumentException("Asesor no encontrado"));
            Asesor asesorUpdate = usuarioMapper.mapToUsuario(request, asesor);

            service.save(asesorUpdate);
            return asesorUpdate != null ? ResponseEntity.ok(asesorUpdate) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ExceptionHandlerUtil.userHandleException(e);
//            String err = e.getMessage();
//            if (err.contains("uk_correo_electronico"))
//                return ResponseEntity.status(HttpStatus.CONFLICT)
//                        .body("Ocurrió un error al persistir la información, el correo electrónico ya le pertenece a otro usuario.");
//
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body("Ocurrió un error al persistir la información.");
        }
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
