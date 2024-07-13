package avalon.usuarios.service;

import avalon.usuarios.data.ClienteRepository;
import avalon.usuarios.model.pojo.Asesor;
import avalon.usuarios.model.pojo.Cliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteServiceImpl extends UsuariosServiceImpl<Cliente> implements ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    public Page<Cliente> findAll(Pageable pageable) {
        return clienteRepository.findAll(pageable);
    }

    @Override
    public Page<Cliente> findAllByEstado(String estado, Pageable pageable) {
        return clienteRepository.findAllByEstado(estado, pageable);
    }

}
