package avalon.usuarios.model.request;

import avalon.usuarios.model.pojo.Direccion;
import jakarta.persistence.Embedded;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CentroMedicoRequest {

	@NotNull
	private String nombre;
	@NotNull
	private String descripcion;
	@Email
	private String correoElectronico;
	@Embedded
	private DireccionRequest direccion;
	// Getters and setters
}
