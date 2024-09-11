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
public class RestartPasswordRequest {

	@NotNull
	private String correoElectronico;
	@NotNull
	private String codigo2FA;
	@NotNull
	private String contraseniaNueva;
	// Getters and setters
}
