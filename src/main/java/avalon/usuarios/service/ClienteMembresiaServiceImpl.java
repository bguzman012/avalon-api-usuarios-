package avalon.usuarios.service;

import avalon.usuarios.data.*;
import avalon.usuarios.model.pojo.*;
import avalon.usuarios.model.request.CreateUsuarioMembresiaRequest;
import avalon.usuarios.model.request.UpdateUsuarioMembresiaRequest;
import avalon.usuarios.model.response.UsuariosMembresiaResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClienteMembresiaServiceImpl implements ClienteMembresiaService {

    private final ClienteMembresiaRepository repository;
    @Autowired
    private MembresiaRepository membresiaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    public ClienteMembresiaServiceImpl(ClienteMembresiaRepository repository) {
        this.repository = repository;
    }

    @Override
    public ClienteMembresia createUsuarioMembresia(CreateUsuarioMembresiaRequest request) {
        Usuario asesor = new Asesor();
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId()).orElse(null);
        asesor = usuarioRepository.findById(request.getAsesorId()).orElse(null);
        Membresia membresia = membresiaRepository.findById(request.getMembresiaId()).orElse(null);

        if (usuario == null || membresia == null || asesor == null)
            return null;

        ClienteMembresia clienteMembresia = new ClienteMembresia();
        clienteMembresia.setMembresia(membresia);
        clienteMembresia.setUsuario(usuario);
        clienteMembresia.setAsesor(asesor);
        return repository.save(clienteMembresia);
    }

    @Override
    public List<ClienteMembresia> getUsuarioMembresias() {
        return repository.findAll();
    }

    @Override
    public List<UsuariosMembresiaResponse> getUsuariosMembresiasByUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        if (usuario == null) return null;

        List<ClienteMembresia> clienteMembresiaList = this.repository.findAllByUsuario(usuario);
        List<UsuariosMembresiaResponse> usuariosMembresiaResponseList = new ArrayList<>();
        for (ClienteMembresia clienteMembresia : clienteMembresiaList){

            UsuariosMembresiaResponse usuariosMembresiaResponse
                    = new UsuariosMembresiaResponse(clienteMembresia.getUsuario(), clienteMembresia.getMembresia(), clienteMembresia.getAsesor());

            usuariosMembresiaResponseList.add(usuariosMembresiaResponse);

        }

        return usuariosMembresiaResponseList;
    }

    @Override
    public List<UsuariosMembresiaResponse> getUsuariosMembresiasByMembresia(Long membresiaId) {
        Membresia membresia = membresiaRepository.findById(membresiaId).orElse(null);
        if (membresia == null) return null;

        List<ClienteMembresia> clienteMembresiaList = this.repository.findAllByMembresia(membresia);
        List<UsuariosMembresiaResponse> usuariosMembresiaResponseList = new ArrayList<>();
        for (ClienteMembresia clienteMembresia : clienteMembresiaList){

            UsuariosMembresiaResponse usuariosMembresiaResponse
                    = new UsuariosMembresiaResponse(clienteMembresia.getUsuario(), clienteMembresia.getMembresia(),
                    clienteMembresia.getAsesor());

            usuariosMembresiaResponseList.add(usuariosMembresiaResponse);

        }

        return usuariosMembresiaResponseList;
    }

    @Override
    public ClienteMembresia getUsuarioMembresia(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public ClienteMembresia updateUsuarioMembresia(ClienteMembresia clienteMembresia, UpdateUsuarioMembresiaRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId()).orElse(null);
        Usuario asesor = usuarioRepository.findById(request.getAsesorId()).orElse(null);
        Membresia membresia = membresiaRepository.findById(request.getMembresiaId()).orElse(null);

        if (usuario == null || membresia == null)
            return null;

        clienteMembresia.setMembresia(membresia);
        clienteMembresia.setUsuario(usuario);
        clienteMembresia.setAsesor(asesor);

        return repository.save(clienteMembresia);
    }

    @Override
    public void deleteUsuarioMembresia(Long id) {
        repository.deleteById(id);
    }
}
