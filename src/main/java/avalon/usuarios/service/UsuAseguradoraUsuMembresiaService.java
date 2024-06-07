package avalon.usuarios.service;

import avalon.usuarios.model.pojo.UsuAseguradoraUsuMembresia;
import avalon.usuarios.model.pojo.UsuarioAseguradora;
import avalon.usuarios.model.request.CreateUsuAseguradoraUsuMembresiaRequest;
import avalon.usuarios.model.request.CreateUsuarioAseguradoraRequest;
import avalon.usuarios.model.request.UpdateUsuAseguradoraUsuMembresiaRequest;
import avalon.usuarios.model.request.UpdateUsuarioAseguradoraRequest;

import java.util.List;

public interface UsuAseguradoraUsuMembresiaService {

    UsuAseguradoraUsuMembresia createUsuAseguradoraUsuMembresia(CreateUsuAseguradoraUsuMembresiaRequest request);

    List<UsuAseguradoraUsuMembresia> getUsuAseguradoraUsuMembresias();

    UsuAseguradoraUsuMembresia getUsuAseguradoraUsuMembresia(Long usuAseguradoraUsuMembresiaId);

    UsuAseguradoraUsuMembresia updateUsuAseguradoraUsuMembresia(UsuAseguradoraUsuMembresia usuAseguradoraUsuMembresia, UpdateUsuAseguradoraUsuMembresiaRequest request);

    void deleteUsuAseguradoraUsuMembresia(Long usuAseguradoraUsuMembresiaId);
}
