package avalon.usuarios.controller;

import avalon.usuarios.model.pojo.Rol;
import avalon.usuarios.model.request.CreateRolRequest;
import avalon.usuarios.service.RolessServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RolController {

    private final RolessServiceImpl service;

    @GetMapping("/roles")
    public ResponseEntity<List<Rol>> getRoles() {
        List<Rol> roles = service.getRoles();

        if (!roles.isEmpty()) {
            return ResponseEntity.ok(roles);
        } else {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @PostMapping("/roles")
    public ResponseEntity<Rol> saveRoles(@RequestBody CreateRolRequest request) {
        try {
            Rol result = service.saveRol(request);
            return result.getId() != null ? ResponseEntity.status(HttpStatus.CREATED).body(result) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/roles/{rolId}")
    public ResponseEntity<Void> deleteRol(@PathVariable Long rolId) {
        Boolean removed = service.deleteRol(rolId);

        if (Boolean.TRUE.equals(removed)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
