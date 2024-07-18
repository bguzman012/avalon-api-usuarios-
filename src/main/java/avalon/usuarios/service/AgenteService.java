package avalon.usuarios.service;

import avalon.usuarios.model.pojo.Agente;
import avalon.usuarios.model.pojo.Asesor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AgenteService extends UsuariosService<Agente> {
    Page<Agente> findAll(Pageable pageable);
    Page<Agente> findAllByEstado(String estado, Pageable pageable);
    Page<Agente> searchAgentes(String estado, String busqueda, Pageable pageable);
}