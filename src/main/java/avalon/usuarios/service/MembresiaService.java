package avalon.usuarios.service;

import avalon.usuarios.model.pojo.Membresia;
import avalon.usuarios.model.pojo.Usuario;
import avalon.usuarios.model.request.*;

import java.util.List;

public interface MembresiaService {

    List<Membresia> getMembresias();
    Membresia getMembresia(Long membresiaId);
    Membresia createMembresia(CreateMembresiaRequest request);
    Membresia updateMembresia(Membresia membresia, UpdateMembresiaRequest request);
    void deleteMembresia(Long membresiaId);
}
