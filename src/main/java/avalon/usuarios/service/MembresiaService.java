package avalon.usuarios.service;

import avalon.usuarios.model.pojo.Beneficio;
import avalon.usuarios.model.pojo.Membresia;
import avalon.usuarios.model.pojo.Poliza;
import avalon.usuarios.model.pojo.Usuario;
import avalon.usuarios.model.request.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MembresiaService {

    List<Membresia> getMembresias();
    List<Membresia> getMembresiasByEstado(String estado);
    Page<Membresia> searchMembresias(String estado, String busqueda, Pageable pageable);
    Optional<Membresia> getMembresia(Long membresiaId);
    Optional<Membresia> getMembresiaByName(String membresiaName);
    Membresia saveMembresia(Membresia membresia);
    void deleteMembresia(Long membresiaId);
}
