package avalon.usuarios.controller;

import avalon.usuarios.mapper.UsuarioMapper;
import avalon.usuarios.model.pojo.Cliente;
import avalon.usuarios.model.pojo.Direccion;
import avalon.usuarios.model.pojo.Estado;
import avalon.usuarios.model.pojo.Pais;
import avalon.usuarios.model.request.ClienteRequest;
import avalon.usuarios.model.request.PartiallyUpdateUsuario;
import avalon.usuarios.service.ClienteService;
import avalon.usuarios.service.EstadosService;
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
public class EstadoController {

    @Autowired
    private EstadosService service;
    @Autowired
    private PaisService paisService;

    @GetMapping("/paises/{paisId}/estados")
    public ResponseEntity<List<Estado>> getEstadosByPais(@PathVariable Long paisId) {
        Pais pais = paisService.findById(paisId).orElseThrow(() -> new IllegalArgumentException("País no encontrado"));
        List<Estado> estados = this.service.findAllByPais(pais);

        if (!estados.isEmpty()) {
            return ResponseEntity.ok(estados);
        } else {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @GetMapping("/estados/{estadoId}")
    public ResponseEntity<Estado> getEstado(@PathVariable Long estadoId) {
        Estado estado = service.findById(estadoId).orElseThrow(() -> new IllegalArgumentException("Estado no encontrado"));

        if (estado != null) {
            return ResponseEntity.ok(estado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}
