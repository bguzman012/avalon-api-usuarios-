package avalon.usuarios.service;

import avalon.usuarios.data.CoberturaRepository;
import avalon.usuarios.data.EspecialidadRepository;
import avalon.usuarios.model.pojo.Cobertura;
import avalon.usuarios.model.pojo.Especialidad;
import avalon.usuarios.model.pojo.Poliza;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EspecialidadServiceImpl implements EspecialidadService {

    private final EspecialidadRepository repository;

    @Autowired
    public EspecialidadServiceImpl(EspecialidadRepository repository) {
        this.repository = repository;
    }

    @Override
    public Page<Especialidad> searchEspecialidades(String busqueda, Pageable pageable) {
        return this.repository.searchEspecialidades(busqueda, pageable);
    }

    @Override
    public Optional<Especialidad> getEspecialidad(Long especialidadId) {
        return this.repository.findById(especialidadId);
    }
}
