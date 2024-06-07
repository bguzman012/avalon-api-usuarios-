package avalon.usuarios.service;

import avalon.usuarios.model.pojo.Aseguradora;
import avalon.usuarios.model.pojo.Usuario;
import avalon.usuarios.model.request.*;

import java.util.List;

public interface AseguradoraService {

    List<Aseguradora> getAseguradoras();
    Aseguradora getAseguradora(Long aseguradoraId);
    Aseguradora createAseguradora(CreateAseguradoraRequest request);
    Aseguradora updateAseguradora(Aseguradora aseguradora, UpdateAseguradoraRequest request);
    void deleteAseguradora(Long aseguradoraId);
}
