package avalon.usuarios.service;

import avalon.usuarios.data.AgenteRepository;
import avalon.usuarios.data.AsesorRepository;
import avalon.usuarios.data.ClienteRepository;
import avalon.usuarios.mapper.UsuarioMapper;
import avalon.usuarios.model.pojo.*;
import avalon.usuarios.model.request.AgenteRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgenteServiceImpl extends UsuariosServiceImpl<Agente> implements AgenteService {

    @Autowired
    private AgenteRepository agenteRepository;

    @Override
    public Page<Agente> findAll(Pageable pageable) {
        return agenteRepository.findAll(pageable);
    }

    @Override
    public Page<Agente> findAllByEstado(String estado, Pageable pageable) {
        return agenteRepository.findAllByEstado(estado, pageable);
    }
}
