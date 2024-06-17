package avalon.usuarios.service;

import avalon.usuarios.model.pojo.Aseguradora;
import avalon.usuarios.model.pojo.Usuario;
import avalon.usuarios.model.request.*;
import avalon.usuarios.model.response.CreateAseguradoraResponse;

import java.util.List;

public interface AseguradoraService {

    List<CreateAseguradoraResponse> getAseguradoraByEstado(String estado, Long tipoEmpresaId);
    List<CreateAseguradoraResponse> getAseguradoraByUsuarioAndEstado(Long usuarioId, String estado);

    Aseguradora getAseguradora(Long aseguradoraId);
    Aseguradora createAseguradora(CreateAseguradoraRequest request);
    Aseguradora updateAseguradora(Aseguradora aseguradora, UpdateAseguradoraRequest request);
    Aseguradora partiallyUpdateAseguradora(PartiallyUpdateAseguradora request, Long aseguradoraId);
    void deleteAseguradora(Long aseguradoraId);
}
