package avalon.usuarios.model.request;

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
public class CreateUsuarioMembresiaRequest {

	@NotNull
	private Long membresiaId;

	@NotNull
	private Long usuarioId;

	// Getters and setters
}
