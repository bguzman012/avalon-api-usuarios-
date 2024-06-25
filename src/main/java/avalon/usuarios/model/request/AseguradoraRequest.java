package avalon.usuarios.model.request;

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
public class AseguradoraRequest {

	@NotNull
	private String nombre;
	@NotNull
	@Email
	private String correoElectronico;
	// Getters and setters
}
