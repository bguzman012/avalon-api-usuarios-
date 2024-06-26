package avalon.usuarios.service;

import avalon.usuarios.model.pojo.Aseguradora;
import avalon.usuarios.model.request.*;

import java.util.List;
import java.util.Optional;

public interface AseguradoraService {

    List<Aseguradora> getAseguradoraByEstado(String estado);
    Optional<Aseguradora> getAseguradora(Long aseguradoraId);
    Aseguradora createAseguradora(Aseguradora aseguradora);
    Aseguradora partiallyUpdateAseguradora(PartiallyUpdateAseguradora request, Long aseguradoraId);
    void deleteAseguradora(Long aseguradoraId);
}
