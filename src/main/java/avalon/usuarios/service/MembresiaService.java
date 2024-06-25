package avalon.usuarios.service;

import avalon.usuarios.model.pojo.Membresia;
import avalon.usuarios.model.pojo.Usuario;
import avalon.usuarios.model.request.*;

import java.util.List;
import java.util.Optional;

public interface MembresiaService {

    List<Membresia> getMembresias();
    List<Membresia> getMembresiasByAseguradora(Long aseguradoraId);

    List<Membresia> getMembresiasByEstado(String estado);

    Optional<Membresia> getMembresia(Long membresiaId);
    Membresia saveMembresia(Membresia membresia);
    void deleteMembresia(Long membresiaId);
}
