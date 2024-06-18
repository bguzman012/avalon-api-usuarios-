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
public class UpdateClientePolizaRequest {

	@NotNull
	private Long clienteId;
	@NotNull
	private Long asesorId;
	@NotNull
	private Long agenteId;
	@NotNull
	private Long polizaId;

}
