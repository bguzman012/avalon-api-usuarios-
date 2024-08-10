package avalon.usuarios.service;

import avalon.usuarios.model.pojo.Caso;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CasoService {

    Page<Caso> searchCasos(String busqueda, Pageable pageable, Long clientePolizaId);
    Optional<Caso> getCaso(Long casoId);
    Caso saveCaso(Caso caso);
    void deleteCaso(Long casoId);

}
