package avalon.usuarios.service;

import avalon.usuarios.data.AsesorRepository;
import avalon.usuarios.data.CargaFamiliarRepository;
import avalon.usuarios.model.pojo.Asesor;
import avalon.usuarios.model.pojo.CargaFamiliar;
import avalon.usuarios.model.pojo.ClientePoliza;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CargaFamiliarServiceImpl implements CargaFamiliarService {

    @Autowired
    private CargaFamiliarRepository repository;


    @Override
    public CargaFamiliar save(CargaFamiliar entity) {
        return this.repository.save(entity);
    }

    @Override
    public Optional<CargaFamiliar> findById(Long id) {
        return this.repository.findById(id);
    }

    @Override
    public List<CargaFamiliar> findAll() {
        return this.repository.findAll();
    }

    @Override
    public List<CargaFamiliar> findAllByClientePoliza(ClientePoliza clientePoliza) {
        return this.repository.findAllByClientePoliza(clientePoliza);
    }

    @Override
    public void deleteById(Long id) {
        this.repository.deleteById(id);
    }
}
