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
public class CargaFamiliarRequest extends ClienteRequest{

	@NotNull
	private String parentesco;
	private Long clienteId;
	@NotNull
	private String numeroCertificado;
	@NotNull
	private Long clientePolizaTitularId;
	// Getters and setters
}
