package avalon.usuarios.service;

import avalon.usuarios.model.request.CreateUsuAseguradoraUsuMembresiaRequest;
import avalon.usuarios.model.request.UpdateUsuAseguradoraUsuMembresiaRequest;

import java.util.List;

public interface UsuAseguradoraUsuMembresiaService {

    UsuAseguradoraUsuMembresia createUsuAseguradoraUsuMembresia(CreateUsuAseguradoraUsuMembresiaRequest request);

    List<UsuAseguradoraUsuMembresia> getUsuAseguradoraUsuMembresias();

    UsuAseguradoraUsuMembresia getUsuAseguradoraUsuMembresia(Long usuAseguradoraUsuMembresiaId);

    UsuAseguradoraUsuMembresia updateUsuAseguradoraUsuMembresia(UsuAseguradoraUsuMembresia usuAseguradoraUsuMembresia, UpdateUsuAseguradoraUsuMembresiaRequest request);

    void deleteUsuAseguradoraUsuMembresia(Long usuAseguradoraUsuMembresiaId);
}
