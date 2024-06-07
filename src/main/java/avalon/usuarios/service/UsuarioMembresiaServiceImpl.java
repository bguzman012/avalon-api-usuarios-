package avalon.usuarios.service;

import avalon.usuarios.data.*;
import avalon.usuarios.model.pojo.*;
import avalon.usuarios.model.request.CreateUsuarioAseguradoraRequest;
import avalon.usuarios.model.request.CreateUsuarioMembresiaRequest;
import avalon.usuarios.model.request.UpdateUsuarioAseguradoraRequest;
import avalon.usuarios.model.request.UpdateUsuarioMembresiaRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioMembresiaServiceImpl implements UsuarioMembresiaService {

    private final UsuarioMembresiaRepository repository;
    @Autowired
    private MembresiaRepository membresiaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioMembresiaServiceImpl(UsuarioMembresiaRepository repository) {
        this.repository = repository;
    }

    @Override
    public UsuarioMembresia createUsuarioMembresia(CreateUsuarioMembresiaRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId()).orElse(null);
        Membresia membresia = membresiaRepository.findById(request.getMembresiaId()).orElse(null);

        if (usuario == null || membresia == null)
            return null;

        UsuarioMembresia usuarioMembresia = new UsuarioMembresia();
        usuarioMembresia.setMembresia(membresia);
        usuarioMembresia.setUsuario(usuario);
        return repository.save(usuarioMembresia);
    }

    @Override
    public List<UsuarioMembresia> getUsuarioMembresias() {
        return repository.findAll();
    }

    @Override
    public UsuarioMembresia getUsuarioMembresia(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public UsuarioMembresia updateUsuarioMembresia(UsuarioMembresia usuarioMembresia, UpdateUsuarioMembresiaRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId()).orElse(null);
        Membresia membresia = membresiaRepository.findById(request.getMembresiaId()).orElse(null);

        if (usuario == null || membresia == null)
            return null;

        usuarioMembresia.setMembresia(membresia);
        usuarioMembresia.setUsuario(usuario);

        return repository.save(usuarioMembresia);
    }

    @Override
    public void deleteUsuarioMembresia(Long id) {
        repository.deleteById(id);
    }
}
