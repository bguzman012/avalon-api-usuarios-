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
public class MedicoRequest {

	@NotNull
	private String nombres;
	private String nombresDos;
	private String estado;
	@NotNull
	private String apellidos;
	private String apellidosDos;
	@NotNull
	private Long especialidadId;
	@NotNull
	private String correoElectronico;
	private String numeroTelefono;
	private String urlImagen;
	private DireccionRequest direccion;
	// Getters and setters
}
