package avalon.usuarios.service;

import avalon.usuarios.model.pojo.Agente;
import avalon.usuarios.model.pojo.Asesor;
import avalon.usuarios.model.pojo.Usuario;

import java.util.List;
import java.util.Optional;

public interface AsesorService extends UsuariosService<Asesor> {
    List<Asesor> findAllByEstado(String estado);
}