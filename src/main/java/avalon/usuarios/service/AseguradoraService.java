package avalon.usuarios.service;

import avalon.usuarios.model.pojo.Aseguradora;
import avalon.usuarios.model.request.*;

import java.util.List;

public interface AseguradoraService {

    List<Aseguradora> getAseguradoraByEstado(String estado);
    Aseguradora getAseguradora(Long aseguradoraId);
    Aseguradora createAseguradora(Aseguradora aseguradora);
    Aseguradora updateAseguradora(Aseguradora aseguradora, AseguradoraRequest request);
    Aseguradora partiallyUpdateAseguradora(PartiallyUpdateAseguradora request, Long aseguradoraId);
    void deleteAseguradora(Long aseguradoraId);
}
