package avalon.usuarios.service;

import avalon.usuarios.model.pojo.Cobertura;
import avalon.usuarios.model.pojo.Especialidad;
import avalon.usuarios.model.pojo.Poliza;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface EspecialidadService {
    Page<Especialidad> searchEspecialidades(String busqueda, Pageable pageable);
    Optional<Especialidad> getEspecialidad(Long especialidadId);

}
