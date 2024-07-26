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
public class CitaMedicaRequest {

	@NotNull
	private String razon;
	private String estado;
	private String nombreDocumento;
	@NotNull
	private Long clientePolizaId;
	@NotNull
	private Long medicoCentroMedicoAseguradoraId;

}