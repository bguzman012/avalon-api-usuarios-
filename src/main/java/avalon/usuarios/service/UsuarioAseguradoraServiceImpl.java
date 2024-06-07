package avalon.usuarios.service;

import avalon.usuarios.data.AseguradoraRepository;
import avalon.usuarios.data.MembresiaRepository;
import avalon.usuarios.data.UsuarioAseguradoraRepository;
import avalon.usuarios.data.UsuarioRepository;
import avalon.usuarios.model.pojo.Aseguradora;
import avalon.usuarios.model.pojo.Membresia;
import avalon.usuarios.model.pojo.Usuario;
import avalon.usuarios.model.pojo.UsuarioAseguradora;
import avalon.usuarios.model.request.CreateMembresiaRequest;
import avalon.usuarios.model.request.CreateUsuarioAseguradoraRequest;
import avalon.usuarios.model.request.UpdateMembresiaRequest;
import avalon.usuarios.model.request.UpdateUsuarioAseguradoraRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioAseguradoraServiceImpl implements UsuarioAseguradoraService {

    private final UsuarioAseguradoraRepository repository;

    @Autowired
    private AseguradoraRepository aseguradoraRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioAseguradoraServiceImpl(UsuarioAseguradoraRepository repository) {
        this.repository = repository;
    }

    @Override
    public UsuarioAseguradora createUsuarioAseguradora(CreateUsuarioAseguradoraRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId()).orElse(null);
        Aseguradora aseguradora = aseguradoraRepository.findById(request.getAseguradoraId()).orElse(null);

        if (usuario == null || aseguradora == null)
            return null;

        UsuarioAseguradora usuarioAseguradora = new UsuarioAseguradora();
        usuarioAseguradora.setAseguradora(aseguradora);
        usuarioAseguradora.setUsuario(usuario);
        return repository.save(usuarioAseguradora);
    }

    @Override
    public List<UsuarioAseguradora> getUsuarioAseguradoras() {
        return repository.findAll();
    }

    @Override
    public UsuarioAseguradora getUsuarioAseguradora(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public UsuarioAseguradora updateUsuarioAseguradora(UsuarioAseguradora usuarioAseguradora, UpdateUsuarioAseguradoraRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId()).orElse(null);
        Aseguradora aseguradora = aseguradoraRepository.findById(request.getAseguradoraId()).orElse(null);

        if (usuario == null || aseguradora == null)
            return null;

        usuarioAseguradora.setAseguradora(aseguradora);
        usuarioAseguradora.setUsuario(usuario);
        return repository.save(usuarioAseguradora);
    }

    @Override
    public void deleteUsuarioAseguradora(Long id) {
        repository.deleteById(id);
    }
}
