package avalon.usuarios.model.response;

import avalon.usuarios.model.pojo.Aseguradora;
import avalon.usuarios.model.pojo.Membresia;
import avalon.usuarios.model.pojo.UsuAseguradoraUsuMembresia;
import avalon.usuarios.model.pojo.Usuario;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsuariosMembresiaResponse {

	private Usuario usuario;
	private Membresia membresia;
	private Aseguradora aseguradora;
	private List<UsuAseguradoraUsuMembresia> usuAseguradoraUsuMembresiaList;

}
