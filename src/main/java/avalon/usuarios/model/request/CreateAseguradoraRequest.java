package avalon.usuarios.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateAseguradoraRequest {

	@NotNull
	private String nombre;
	@NotNull
	@Email
	private String correoElectronico;
	private Long tipoAseguradoraId;
	// Getters and setters
}
