package avalon.usuarios.service;

import avalon.usuarios.data.AgenteRepository;
import avalon.usuarios.data.AsesorRepository;
import avalon.usuarios.mapper.UsuarioMapper;
import avalon.usuarios.model.pojo.Agente;
import avalon.usuarios.model.pojo.Asesor;
import avalon.usuarios.model.pojo.Rol;
import avalon.usuarios.model.pojo.Usuario;
import avalon.usuarios.model.request.AgenteRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AgenteServiceImpl extends UsuariosServiceImpl<Agente> implements AgenteService {

}
