package avalon.usuarios.service;

import avalon.usuarios.data.*;
import avalon.usuarios.model.pojo.*;
import avalon.usuarios.model.request.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
    public Page<Aseguradora> searchAseguradoras(String estado, String busqueda, Pageable pageable) {
        return repository.searchAseguradoras(estado, busqueda, pageable);
    }

    @Override
    public Optional<Aseguradora> getAseguradora(Long aseguradoraId) {
        return repository.findById(aseguradoraId);
    }

    @Override
    public Optional<Aseguradora> getAseguradoraByNombre(String nombre) {
        return repository.findByNombreContainingIgnoreCase(nombre);
    }

    @Override
    public Aseguradora createAseguradora(Aseguradora aseguradora) {
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
