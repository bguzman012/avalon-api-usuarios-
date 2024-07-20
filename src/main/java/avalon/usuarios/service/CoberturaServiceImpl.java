package avalon.usuarios.service;

import avalon.usuarios.data.CoberturaRepository;
import avalon.usuarios.model.pojo.Cobertura;
import avalon.usuarios.model.pojo.Membresia;
import avalon.usuarios.model.pojo.Poliza;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CoberturaServiceImpl implements CoberturaService {

    private final CoberturaRepository repository;

    @Autowired
    public CoberturaServiceImpl(CoberturaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Cobertura> getCoberturas() {
        return repository.findAll();
    }

    @Override
    public List<Cobertura> getCoberturasByPoliza(Poliza poliza) {
        return repository.findAllByPoliza(poliza);
    }

    @Override
    public Page<Cobertura> searchCoberturasByPoliza(String busqueda, Pageable pageable, Poliza poliza) {
        return repository.searchCoberturasByPoliza(busqueda, poliza, pageable);
    }

    @Override
    public Optional<Cobertura> getCobertura(Long beneficioId) {
        return repository.findById(beneficioId);
    }

    @Override
    public Cobertura saveCobertura(Cobertura beneficio) {
        return repository.save(beneficio);
    }

    @Override
    public void deleteCobertura(Long beneficioId) {
        repository.deleteById(beneficioId);
    }

}
