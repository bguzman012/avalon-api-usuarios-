package avalon.usuarios.service;

import avalon.usuarios.data.AseguradoraRepository;
import avalon.usuarios.data.PolizaRepository;
import avalon.usuarios.model.pojo.Aseguradora;
import avalon.usuarios.model.pojo.Poliza;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PolizasServiceImpl implements PolizaService {

    @Autowired
    private AseguradoraRepository aseguradoraRepository;
    private final PolizaRepository repository;

    @Autowired
    public PolizasServiceImpl(PolizaRepository repository) {
        this.repository = repository;
    }


    @Override
    public List<Poliza> getPolizas() {
        return this.repository.findAll();
    }

    @Override
    public List<Poliza> getPolizasByAseguradora(Long aseguradoraId) {
        Aseguradora aseguradora = this.aseguradoraRepository.findById(aseguradoraId).orElse(null);

        if (aseguradora == null) return null;

        return this.repository.findAllByAseguradora(aseguradora);
    }

    @Override
    public Optional<Poliza> getPoliza(Long polizaId) {
        return this.repository.findById(polizaId);
    }

    @Override
    public Poliza savePoliza(Poliza poliza) {
        return this.repository.save(poliza);
    }

    @Override
    public void deletePoliza(Long polizaId) {
        Poliza poliza = this.getPoliza(polizaId).orElseThrow(() -> new IllegalArgumentException("Poliza no encontrada"));
        this.repository.delete(poliza);

    }
}
