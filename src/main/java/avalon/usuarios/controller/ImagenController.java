package avalon.usuarios.controller;

import avalon.usuarios.model.pojo.*;
import avalon.usuarios.model.request.ImagenRequest;
import avalon.usuarios.model.request.PolizaRequest;
import avalon.usuarios.model.request.ReclamacionRequest;
import avalon.usuarios.service.ImagenService;
import avalon.usuarios.service.ImagenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ImagenController {

    @Autowired
    private ImagenService service;

    @GetMapping("/imagenes/{imagenId}")
    public ResponseEntity<Imagen> getImagen(@PathVariable Long imagenId) {
        Imagen pais = service.findImagenById(imagenId).orElseThrow(() -> new IllegalArgumentException("Imagen no encontrada"));

        if (pais != null) {
            return ResponseEntity.ok(pais);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/imagenes")
    public ResponseEntity<Imagen> createImagen(@RequestPart("imagen") ImagenRequest request,
                                                         @RequestPart("documento") MultipartFile documento) {
        try {
            Imagen imagen = this.mapToImagen(request, new Imagen());

            if (!documento.isEmpty()) {
                imagen.setDocumento(documento.getBytes());
            }

            service.saveImagen(imagen);
            return imagen.getId() != null ? ResponseEntity.status(HttpStatus.CREATED).body(imagen) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/imagenes/{imagenId}")
    public ResponseEntity<Imagen> updateImagen(@PathVariable Long imagenId,
                                                         @RequestPart("imagen") ImagenRequest request,
                                                         @RequestPart("documento") MultipartFile documento) {
        try {
            Imagen imagen = service.findImagenById(imagenId).orElseThrow(() -> new IllegalArgumentException("Imágen no encontrada"));
            Imagen imagenMapped = this.mapToImagen(request, imagen);

            if (!documento.isEmpty()) {
                imagen.setDocumento(documento.getBytes());
            }

            service.saveImagen(imagen);
            return imagen.getId() != null ? ResponseEntity.ok(imagen) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private Imagen mapToImagen(ImagenRequest request, Imagen imagen) {
        imagen.setNombreDocumento(request.getNombreDocumento());
        imagen.setTopico(request.getTopico());
        return imagen;
    }

}
