package avalon.usuarios.service;

import avalon.usuarios.data.AseguradoraRepository;
import avalon.usuarios.data.RolRepository;
import avalon.usuarios.data.UsuarioRepository;
import avalon.usuarios.model.pojo.Aseguradora;
import avalon.usuarios.model.pojo.Rol;
import avalon.usuarios.model.pojo.Usuario;
import avalon.usuarios.model.request.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AseguradoraServiceImpl implements AseguradoraService {

    private final AseguradoraRepository repository;

    @Autowired
    public AseguradoraServiceImpl(AseguradoraRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Aseguradora> getAseguradoras() {
        return repository.findAll();
    }

    @Override
    public Aseguradora getAseguradora(Long aseguradoraId) {
        return repository.findById(aseguradoraId).orElse(null);
    }

    @Override
    public Aseguradora createAseguradora(CreateAseguradoraRequest request) {
        Aseguradora aseguradora = new Aseguradora();
        aseguradora.setNombres(request.getNombres());
        aseguradora.setCorreoElectronico(request.getCorreoElectronico());
        return repository.save(aseguradora);
    }

    @Override
    public Aseguradora updateAseguradora(Aseguradora aseguradora, UpdateAseguradoraRequest request) {
        aseguradora.setNombres(request.getNombres());
        aseguradora.setCorreoElectronico(request.getCorreoElectronico());
        return repository.save(aseguradora);
    }

    @Override
    public void deleteAseguradora(Long aseguradoraId) {
        repository.deleteById(aseguradoraId);
    }

}
