package avalon.usuarios.service;

import avalon.usuarios.model.pojo.ClienteMembresia;
import avalon.usuarios.model.request.CreateUsuarioMembresiaRequest;
import avalon.usuarios.model.request.UpdateUsuarioMembresiaRequest;
import avalon.usuarios.model.response.UsuariosMembresiaResponse;

import java.util.List;

public interface ClienteMembresiaService {

    ClienteMembresia createUsuarioMembresia(CreateUsuarioMembresiaRequest request);
    List<ClienteMembresia> getUsuarioMembresias();
    List<UsuariosMembresiaResponse> getUsuariosMembresiasByMembresia(Long membresiaId);
    List<UsuariosMembresiaResponse> getUsuariosMembresiasByUsuario(Long usuarioId);
    ClienteMembresia getUsuarioMembresia(Long id);
    ClienteMembresia updateUsuarioMembresia(ClienteMembresia clienteMembresia, UpdateUsuarioMembresiaRequest request);
    void deleteUsuarioMembresia(Long id);
}
