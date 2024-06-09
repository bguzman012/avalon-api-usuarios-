package avalon.usuarios.model.response;

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
public class CreateAseguradoraResponse {

	private Long id;
	private String nombre;
	private String correoElectronico;
	private String estado;
	// Getters and setters
}
