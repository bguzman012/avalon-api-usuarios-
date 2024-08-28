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
public class ChangePasswordRequest {

	@NotNull
	private String usuario;
	@NotNull
	private String contraseniaActual;
	@NotNull
	private String contraseniaNueva;
	// Getters and setters
}
