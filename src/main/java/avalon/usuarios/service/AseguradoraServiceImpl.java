package avalon.usuarios.service;

import avalon.usuarios.data.*;
import avalon.usuarios.model.pojo.*;
import avalon.usuarios.model.request.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AseguradoraServiceImpl implements AseguradoraService {

    private final AseguradoraRepository repository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    public AseguradoraServiceImpl(AseguradoraRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Aseguradora> getAseguradoraByEstado(String estado) {
        return repository.findAllByEstado(estado);
    }

    @Override
    public Aseguradora getAseguradora(Long aseguradoraId) {
        return repository.findById(aseguradoraId).orElse(null);
    }

    @Override
    public Aseguradora createAseguradora(Aseguradora aseguradora) {
        return repository.save(aseguradora);
    }

    @Override
    public Aseguradora updateAseguradora(Aseguradora aseguradora, AseguradoraRequest request) {
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
