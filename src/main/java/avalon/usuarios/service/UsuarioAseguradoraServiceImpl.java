package avalon.usuarios.service;

import avalon.usuarios.data.*;
import avalon.usuarios.model.pojo.*;
import avalon.usuarios.model.request.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioAseguradoraServiceImpl implements UsuarioAseguradoraService {

    private final UsuarioAseguradoraRepository repository;

    @Autowired
    private AseguradoraRepository aseguradoraRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private RolRepository rolRepository;


    @Autowired
    public UsuarioAseguradoraServiceImpl(UsuarioAseguradoraRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<UsuarioAseguradora> createListUsuarioAseguradora(CreateListUsuarioAseguradoraRequest request) {
        List<UsuarioAseguradora> usuarioAseguradoraList = new java.util.ArrayList<>(Collections.emptyList());
        for (CreateUsuarioAseguradoraRequest createUsuarioAseguradoraRequest: request.getUsuariosAseguradoras()) {
            Usuario usuario = usuarioRepository.findById(createUsuarioAseguradoraRequest.getUsuarioId()).orElse(null);
            Aseguradora aseguradora = aseguradoraRepository.findById(createUsuarioAseguradoraRequest.getAseguradoraId()).orElse(null);

            if (usuario == null || aseguradora == null)
                continue;

            UsuarioAseguradora usuarioAseguradoraExiste = repository.findByAseguradoraAndUsuario(aseguradora, usuario);
            if (usuarioAseguradoraExiste != null)
                continue;

            UsuarioAseguradora usuarioAseguradora = new UsuarioAseguradora();
            usuarioAseguradora.setAseguradora(aseguradora);
            usuarioAseguradora.setUsuario(usuario);
            usuarioAseguradora.setEstado("A");
            repository.save(usuarioAseguradora);
            usuarioAseguradoraList.add(usuarioAseguradora);
        }

        return usuarioAseguradoraList;
    }


    @Override
    public List<UsuarioAseguradora> updateListUsuariosAseguradoras(CreateListUsuarioAseguradoraRequest request, Long usuarioId) {
        List<UsuarioAseguradora> usuarioAseguradoraList = new java.util.ArrayList<>(Collections.emptyList());

        Usuario usuarioOwner = this.usuarioRepository.findById(usuarioId).orElse(null);
        List<UsuarioAseguradora> usuarioAseguradorasExistentesIds = this.repository.findByUsuarioAndEstado(usuarioOwner, "A");

        List<Long> usuariosAseguradorasNuevosIds = request.getUsuariosAseguradoras().stream()
                .map(CreateUsuarioAseguradoraRequest::getAseguradoraId)
                .toList();

        List<UsuarioAseguradora> difference = usuarioAseguradorasExistentesIds.stream()
                .filter(aseguradora -> !usuariosAseguradorasNuevosIds.contains(aseguradora.getAseguradora().getId()))
                .toList();

        for (UsuarioAseguradora usuarioAsegurador: difference) {
            usuarioAsegurador.setEstado("I");
            repository.save(usuarioAsegurador);
        }

        for (CreateUsuarioAseguradoraRequest createUsuarioAseguradoraRequest: request.getUsuariosAseguradoras()) {
            Usuario usuario = usuarioRepository.findById(createUsuarioAseguradoraRequest.getUsuarioId()).orElse(null);
            Aseguradora aseguradora = aseguradoraRepository.findById(createUsuarioAseguradoraRequest.getAseguradoraId()).orElse(null);

            if (usuario == null || aseguradora == null)
                continue;

            UsuarioAseguradora usuarioAseguradoraExiste = repository.findByAseguradoraAndUsuario(aseguradora, usuario);
            if (usuarioAseguradoraExiste != null)
                continue;

            UsuarioAseguradora usuarioAseguradora = new UsuarioAseguradora();
            usuarioAseguradora.setAseguradora(aseguradora);
            usuarioAseguradora.setUsuario(usuario);
            usuarioAseguradora.setEstado("A");
            repository.save(usuarioAseguradora);
            usuarioAseguradoraList.add(usuarioAseguradora);
        }

        return usuarioAseguradoraList;
    }

    @Override
    public List<UsuarioAseguradora> getUsuarioAseguradoras() {
        return repository.findAll();
    }

    @Override
    public List<UsuarioAseguradora> getUsuarioAseguradorasByAseguradoraAndRol(Long aseguradorId, Long rolId, String estado) {
        Rol rol = rolRepository.findById(rolId).orElse(null);
        if (rol == null) return null;

        Aseguradora aseguradora = aseguradoraRepository.findById(aseguradorId).orElse(null);
        if (aseguradora == null) return null;

        return repository.findAllByAseguradoraAndEstadoAndRol(estado, aseguradora, rol);
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
