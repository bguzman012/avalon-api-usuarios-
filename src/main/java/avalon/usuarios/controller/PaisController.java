package avalon.usuarios.controller;

import avalon.usuarios.mapper.UsuarioMapper;
import avalon.usuarios.model.pojo.Cliente;
import avalon.usuarios.model.pojo.Direccion;
import avalon.usuarios.model.pojo.Pais;
import avalon.usuarios.model.request.ClienteRequest;
import avalon.usuarios.model.request.PartiallyUpdateUsuario;
import avalon.usuarios.service.ClienteService;
import avalon.usuarios.service.PaisService;
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
public class PaisController {

    @Autowired
    private PaisService service;

    @GetMapping("/paises")
    public ResponseEntity<List<Pais>> getPaises() {
        List<Pais> paises = service.findAll();

        if (!paises.isEmpty()) {
            return ResponseEntity.ok(paises);
        } else {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @GetMapping("/paises/{paisId}")
    public ResponseEntity<Pais> getPais(@PathVariable Long paisId) {
        Pais pais = service.findById(paisId).orElseThrow(() -> new IllegalArgumentException("País no encontrado"));

        if (pais != null) {
            return ResponseEntity.ok(pais);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
