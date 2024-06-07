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
public class CreateUsuAseguradoraUsuMembresiaRequest {

	@NotNull
	private Long usuarioAseguradoraId;
	@NotNull
	private Long usuarioMembresiaId;

	// Getters and setters
}
