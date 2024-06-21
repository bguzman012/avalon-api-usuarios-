package avalon.usuarios.service;

import avalon.usuarios.data.*;
import avalon.usuarios.model.pojo.*;
import avalon.usuarios.model.request.CreateUsuAseguradoraUsuMembresiaRequest;
import avalon.usuarios.model.request.UpdateUsuAseguradoraUsuMembresiaRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuAseguradoraUsuMembresiaServiceImpl implements UsuAseguradoraUsuMembresiaService {

    private final UsuAseguradoraUsuMembresiaRepository repository;
    @Autowired
    private UsuarioAseguradoraRepository usuarioAseguradoraRepository;
    @Autowired
    private ClienteMembresiaRepository usuarioMembresiaRepository;

    @Autowired
    public UsuAseguradoraUsuMembresiaServiceImpl(UsuAseguradoraUsuMembresiaRepository repository) {
        this.repository = repository;
    }

    @Override
    public UsuAseguradoraUsuMembresia createUsuAseguradoraUsuMembresia(CreateUsuAseguradoraUsuMembresiaRequest request) {
        UsuarioAseguradora usuarioAseguradora = usuarioAseguradoraRepository.findById(request.getUsuarioAseguradoraId()).orElse(null);
        ClienteMembresia clienteMembresia = usuarioMembresiaRepository.findById(request.getUsuarioMembresiaId()).orElse(null);

        if (usuarioAseguradora == null || clienteMembresia == null)
            return null;

        UsuAseguradoraUsuMembresia usuAseguradoraUsuMembresia = new UsuAseguradoraUsuMembresia();
        usuAseguradoraUsuMembresia.setUsuarioAseguradora(usuarioAseguradora);
        usuAseguradoraUsuMembresia.setUsuarioMembresia(clienteMembresia);

        return repository.save(usuAseguradoraUsuMembresia);
    }

    @Override
    public List<UsuAseguradoraUsuMembresia> getUsuAseguradoraUsuMembresias() {
        return repository.findAll();
    }

    @Override
    public UsuAseguradoraUsuMembresia getUsuAseguradoraUsuMembresia(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public UsuAseguradoraUsuMembresia updateUsuAseguradoraUsuMembresia(UsuAseguradoraUsuMembresia usuAseguradoraUsuMembresia, UpdateUsuAseguradoraUsuMembresiaRequest request) {
        UsuarioAseguradora usuarioAseguradora = usuarioAseguradoraRepository.findById(request.getUsuarioAseguradoraId()).orElse(null);
        ClienteMembresia clienteMembresia = usuarioMembresiaRepository.findById(request.getUsuarioMembresiaId()).orElse(null);

        if (usuarioAseguradora == null || clienteMembresia == null)
            return null;

        usuAseguradoraUsuMembresia.setUsuarioAseguradora(usuarioAseguradora);
        usuAseguradoraUsuMembresia.setUsuarioMembresia(clienteMembresia);

        return repository.save(usuAseguradoraUsuMembresia);
    }

    @Override
    public void deleteUsuAseguradoraUsuMembresia(Long id) {
        repository.deleteById(id);
    }
}
