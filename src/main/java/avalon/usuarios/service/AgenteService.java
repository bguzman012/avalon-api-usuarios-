package avalon.usuarios.service;

import avalon.usuarios.model.pojo.Agente;
import avalon.usuarios.model.pojo.Asesor;

import java.util.List;

public interface AgenteService extends UsuariosService<Agente> {
    List<Agente> findAllByEstado(String estado);
}