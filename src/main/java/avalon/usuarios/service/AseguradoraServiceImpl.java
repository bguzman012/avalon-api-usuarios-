package avalon.usuarios.service;

import avalon.usuarios.data.*;
import avalon.usuarios.model.pojo.*;
import avalon.usuarios.model.request.*;
import avalon.usuarios.model.response.CreateAseguradoraResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AseguradoraServiceImpl implements AseguradoraService {

    private final AseguradoraRepository repository;

    @Autowired
    private UsuarioAseguradoraRepository usuarioAseguradoraRepository;
    @Autowired
    private TipoAseguradoraRepository tipoAseguradoraRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    public AseguradoraServiceImpl(AseguradoraRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<CreateAseguradoraResponse> getAseguradoraByEstado(String estado, Long tipoEmpresaId) {
        TipoAseguradora tipoAseguradora = this.tipoAseguradoraRepository.findById(tipoEmpresaId).orElse(null);

        List <CreateAseguradoraResponse> createAseguradoraResponseList = new ArrayList<>();
        for (Aseguradora aseg : repository.findAllByEstadoAndTipoAseguradora(estado, tipoAseguradora)
        ) {
            CreateAseguradoraResponse createAseguradoraResponse = new CreateAseguradoraResponse();
            createAseguradoraResponse.setId(aseg.getId());
            createAseguradoraResponse.setEstado(aseg.getEstado());
            createAseguradoraResponse.setNombre(aseg.getNombre());
            createAseguradoraResponse.setCorreoElectronico(aseg.getCorreoElectronico());
            createAseguradoraResponseList.add(createAseguradoraResponse);
        }
        return createAseguradoraResponseList;
    }

    @Override
    public List<CreateAseguradoraResponse> getAseguradoraByUsuarioAndEstado(Long usuarioId, String estado) {
        List <CreateAseguradoraResponse> createAseguradoraResponseList = new ArrayList<>();

        Usuario usuario = this.usuarioRepository.findById(usuarioId).orElse(null);

        if (usuario  == null) return createAseguradoraResponseList;

        List<UsuarioAseguradora> usuarioAseguradoraList = this.usuarioAseguradoraRepository.findByUsuarioAndEstado(usuario, "A");

        for (UsuarioAseguradora usuarioAseguradora : usuarioAseguradoraList
        ) {
            CreateAseguradoraResponse createAseguradoraResponse = new CreateAseguradoraResponse();
            createAseguradoraResponse.setId(usuarioAseguradora.getAseguradora().getId());
            createAseguradoraResponse.setEstado(usuarioAseguradora.getAseguradora().getEstado());
            createAseguradoraResponse.setNombre(usuarioAseguradora.getAseguradora().getNombre());
            createAseguradoraResponse.setCorreoElectronico(usuarioAseguradora.getAseguradora().getCorreoElectronico());
            createAseguradoraResponseList.add(createAseguradoraResponse);
        }
        return createAseguradoraResponseList;
    }

    @Override
    public Aseguradora getAseguradora(Long aseguradoraId) {
        return repository.findById(aseguradoraId).orElse(null);
    }

    @Override
    public Aseguradora createAseguradora(CreateAseguradoraRequest request) {
        TipoAseguradora tipoAseguradora = this.tipoAseguradoraRepository.findById(request.getTipoAseguradoraId()).orElse(null);

        Aseguradora aseguradora = new Aseguradora();
        aseguradora.setNombre(request.getNombre());
        aseguradora.setCorreoElectronico(request.getCorreoElectronico());
        aseguradora.setEstado("A");
        aseguradora.setTipoAseguradora(tipoAseguradora);

        return repository.save(aseguradora);
    }

    @Override
    public Aseguradora updateAseguradora(Aseguradora aseguradora, UpdateAseguradoraRequest request) {
        aseguradora.setNombre(request.getNombre());
        aseguradora.setCorreoElectronico(request.getCorreoElectronico());
        return repository.save(aseguradora);
    }

    @Override
    public Aseguradora partiallyUpdateAseguradora(PartiallyUpdateAseguradora request, Long aseguradoraId) {
        Aseguradora aseguradora = repository.findById(aseguradoraId).orElse(null);
        if (aseguradora == null) return null;

        if (request.getEstado() != null)
            aseguradora.setEstado(request.getEstado());

        return repository.save(aseguradora);
    }

    @Override
    public void deleteAseguradora(Long aseguradoraId) {
        repository.deleteById(aseguradoraId);
    }

}
