package avalon.usuarios.service;

import avalon.usuarios.model.pojo.Membresia;
import avalon.usuarios.model.pojo.UsuarioAseguradora;
import avalon.usuarios.model.request.CreateMembresiaRequest;
import avalon.usuarios.model.request.CreateUsuarioAseguradoraRequest;
import avalon.usuarios.model.request.UpdateMembresiaRequest;
import avalon.usuarios.model.request.UpdateUsuarioAseguradoraRequest;

import java.util.List;

public interface UsuarioAseguradoraService {

    UsuarioAseguradora createUsuarioAseguradora(CreateUsuarioAseguradoraRequest request);
    List<UsuarioAseguradora> getUsuarioAseguradoras();
    UsuarioAseguradora getUsuarioAseguradora(Long id);
    UsuarioAseguradora updateUsuarioAseguradora(UsuarioAseguradora usuarioAseguradora, UpdateUsuarioAseguradoraRequest request);
    void deleteUsuarioAseguradora(Long id);
}
