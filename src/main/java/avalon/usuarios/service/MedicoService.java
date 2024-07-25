package avalon.usuarios.service;

import avalon.usuarios.model.pojo.Medico;
import avalon.usuarios.model.pojo.ClientePoliza;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MedicoService {
    Medico save(Medico medico);
    Optional<Medico> findById(Long id);
    Page<Medico> searchMedicos(String busqueda, Pageable pageable);
    void deleteById(Long id);
}