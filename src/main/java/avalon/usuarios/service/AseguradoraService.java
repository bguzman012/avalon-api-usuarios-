package avalon.usuarios.service;

import avalon.usuarios.model.pojo.Aseguradora;
import avalon.usuarios.model.pojo.Broker;
import avalon.usuarios.model.request.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface AseguradoraService {

    Page<Aseguradora> searchAseguradoras(String estado, String busqueda, Pageable pageable);
    Optional<Aseguradora> getAseguradora(Long aseguradoraId);
    Aseguradora createAseguradora(Aseguradora aseguradora);
    Aseguradora partiallyUpdateAseguradora(PartiallyUpdateAseguradora request, Long aseguradoraId);
    void deleteAseguradora(Long aseguradoraId);
}
