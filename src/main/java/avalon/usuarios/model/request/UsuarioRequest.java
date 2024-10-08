package avalon.usuarios.model.request;

import avalon.usuarios.model.pojo.TipoIdentificacion;
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
	private String nombreUsuario;
	private String urlImagen;
	private String numeroIdentificacion;
	private TipoIdentificacion tipoIdentificacion;
	@NotNull
	private String estado;
	@NotNull
	private Long rolId;

	// Getters and setters
}
