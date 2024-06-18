package avalon.usuarios.service;

import avalon.usuarios.data.AseguradoraRepository;
import avalon.usuarios.data.MembresiaRepository;
import avalon.usuarios.data.PolizaRepository;
import avalon.usuarios.model.pojo.Aseguradora;
import avalon.usuarios.model.pojo.Membresia;
import avalon.usuarios.model.pojo.Poliza;
import avalon.usuarios.model.request.CreateMembresiaRequest;
import avalon.usuarios.model.request.CreatePolizaRequest;
import avalon.usuarios.model.request.UpdateMembresiaRequest;
import avalon.usuarios.model.request.UpdatePolizaRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public Poliza getPoliza(Long polizaId) {
        return this.repository.findById(polizaId).orElse(null);
    }

    @Override
    public Poliza createPoliza(CreatePolizaRequest request) {
        Aseguradora aseguradora = this.aseguradoraRepository.findById(request.getAseguradoraId()).orElse(null);
        if (aseguradora == null) return null;

        Poliza poliza = new Poliza();
        poliza.setNombre(request.getNombre());
        poliza.setDescripcion(request.getDescripcion());
        poliza.setVigenciaMeses(request.getVigenciaMeses());
        poliza.setAseguradora(aseguradora);
        return this.repository.save(poliza);
    }

    @Override
    public Poliza updatePoliza(Poliza poliza, UpdatePolizaRequest request) {
        Aseguradora aseguradora = this.aseguradoraRepository.findById(request.getAseguradoraId()).orElse(null);
        if (aseguradora == null) return null;

        poliza.setNombre(request.getNombre());
        poliza.setDescripcion(request.getDescripcion());
        poliza.setVigenciaMeses(request.getVigenciaMeses());
        poliza.setAseguradora(aseguradora);
        return this.repository.save(poliza);

    }

    @Override
    public void deletePoliza(Long polizaId) {
        Poliza poliza = this.getPoliza(polizaId);
        this.repository.delete(poliza);

    }
}
