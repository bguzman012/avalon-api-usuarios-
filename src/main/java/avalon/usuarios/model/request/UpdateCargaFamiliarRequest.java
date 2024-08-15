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
public class UpdateCargaFamiliarRequest{

	@NotNull
	private String parentesco;
	@NotNull
	private Long clienteId;
	@NotNull
	private String numeroCertificado;
	// Getters and setters
}
