package avalon.usuarios.service;

import avalon.usuarios.data.AseguradoraRepository;
import avalon.usuarios.data.BeneficioRepository;
import avalon.usuarios.data.UsuarioRepository;
import avalon.usuarios.model.pojo.Aseguradora;
import avalon.usuarios.model.pojo.Beneficio;
import avalon.usuarios.model.pojo.Membresia;
import avalon.usuarios.model.request.PartiallyUpdateAseguradora;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BeneficioServiceImpl implements BeneficioService {

    private final BeneficioRepository repository;

    @Autowired
    public BeneficioServiceImpl(BeneficioRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Beneficio> getBeneficios() {
        return repository.findAll();
    }

    @Override
    public List<Beneficio> getBeneficiosByMembresia(Membresia membresia) {
        return repository.findAllByMembresia(membresia);
    }

    @Override
    public Page<Beneficio> searchBeneficiosByMembresia(String busqueda, Pageable pageable, Membresia membresia) {
        return repository.searchBeneficiosByMembresia(busqueda, membresia, pageable);
    }

    @Override
    public Optional<Beneficio> getBeneficio(Long beneficioId) {
        return repository.findById(beneficioId);
    }

    @Override
    public Beneficio saveBeneficio(Beneficio beneficio) {
        return repository.save(beneficio);
    }

    @Override
    public void deleteBeneficio(Long beneficioId) {
        repository.deleteById(beneficioId);
    }

}
