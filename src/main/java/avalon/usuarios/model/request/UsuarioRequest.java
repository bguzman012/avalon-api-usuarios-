package avalon.usuarios.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioRequest {

	@NotNull
	private String nombres;
	private String nombresDos;
	@NotNull
	private String apellidos;
	private String apellidosDos;
	@NotNull
	private String correoElectronico;
	@NotNull
	private String numeroTelefono;
	@NotNull
	private String nombreUsuario;
	private String urlImagen;
	@NotNull
	private String estado;
	@NotNull
	private Long rolId;

	// Getters and setters
}
