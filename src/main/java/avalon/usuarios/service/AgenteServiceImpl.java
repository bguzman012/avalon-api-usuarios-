package avalon.usuarios.service;

import avalon.usuarios.data.AgenteRepository;
import avalon.usuarios.data.AsesorRepository;
import avalon.usuarios.data.ClienteRepository;
import avalon.usuarios.mapper.UsuarioMapper;
import avalon.usuarios.model.pojo.*;
import avalon.usuarios.model.request.AgenteRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgenteServiceImpl extends UsuariosServiceImpl<Agente> implements AgenteService {

    @Autowired
    private AgenteRepository agenteRepository;

    @Override
    public List<Agente> findAllByEstado(String estado) {
        return agenteRepository.findAllByEstado(estado);
    }

}
