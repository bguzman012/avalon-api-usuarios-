package avalon.usuarios.service;

import avalon.usuarios.model.pojo.Membresia;
import avalon.usuarios.model.pojo.UsuarioAseguradora;
import avalon.usuarios.model.request.*;

import java.util.List;

public interface UsuarioAseguradoraService {

    List<UsuarioAseguradora> createListUsuarioAseguradora(CreateListUsuarioAseguradoraRequest request);
    List<UsuarioAseguradora> updateListUsuariosAseguradoras(CreateListUsuarioAseguradoraRequest request,  Long usuarioId);
    List<UsuarioAseguradora> getUsuarioAseguradoras();
    UsuarioAseguradora getUsuarioAseguradora(Long id);
    UsuarioAseguradora updateUsuarioAseguradora(UsuarioAseguradora usuarioAseguradora, UpdateUsuarioAseguradoraRequest request);
    void deleteUsuarioAseguradora(Long id);
}
