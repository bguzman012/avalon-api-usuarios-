package avalon.usuarios.service;

import avalon.usuarios.model.pojo.UsuarioAseguradora;
import avalon.usuarios.model.pojo.UsuarioMembresia;
import avalon.usuarios.model.request.CreateUsuarioAseguradoraRequest;
import avalon.usuarios.model.request.CreateUsuarioMembresiaRequest;
import avalon.usuarios.model.request.UpdateUsuarioAseguradoraRequest;
import avalon.usuarios.model.request.UpdateUsuarioMembresiaRequest;
import avalon.usuarios.model.response.UsuariosMembresiaResponse;

import java.util.List;

public interface UsuarioMembresiaService {

    UsuarioMembresia createUsuarioMembresia(CreateUsuarioMembresiaRequest request);
    List<UsuarioMembresia> getUsuarioMembresias();
    List<UsuariosMembresiaResponse> getUsuariosMembresiasByMembresia(Long membresiaId);
    List<UsuariosMembresiaResponse> getUsuariosMembresiasByUsuario(Long usuarioId);
    UsuarioMembresia getUsuarioMembresia(Long id);
    UsuarioMembresia updateUsuarioMembresia(UsuarioMembresia usuarioMembresia, UpdateUsuarioMembresiaRequest request);
    void deleteUsuarioMembresia(Long id);
}
