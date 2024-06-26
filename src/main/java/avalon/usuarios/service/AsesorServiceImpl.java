package avalon.usuarios.service;

import avalon.usuarios.data.AgenteRepository;
import avalon.usuarios.data.AsesorRepository;
import avalon.usuarios.data.BaseUsuarioRepository;
import avalon.usuarios.data.ClienteRepository;
import avalon.usuarios.model.pojo.Asesor;
import avalon.usuarios.model.pojo.Cliente;
import avalon.usuarios.model.pojo.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AsesorServiceImpl extends UsuariosServiceImpl<Asesor> implements AsesorService {

    @Autowired
    private AsesorRepository asesorRepository;

    @Override
    public List<Asesor> findAllByEstado(String estado) {
        return asesorRepository.findAllByEstado(estado);
    }
}
