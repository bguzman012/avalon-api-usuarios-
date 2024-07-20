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
public class CargaFamiliarRequest {

	@NotNull
	private String nombres;
	private String nombresDos;
	@NotNull
	private String apellidos;
	private String apellidosDos;
	@NotNull
	private String parentesco;
	@NotNull
	private String correoElectronico;
	private String numeroTelefono;
	private String urlImagen;
	@NotNull
	private Long clientePolizaId;
	// Getters and setters
}
