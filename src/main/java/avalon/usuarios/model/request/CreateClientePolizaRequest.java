package avalon.usuarios.model.request;

import avalon.usuarios.model.pojo.Poliza;
import avalon.usuarios.model.pojo.Usuario;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateClientePolizaRequest {

	@NotNull
	private Long clienteId;
	@NotNull
	private Long asesorId;
	@NotNull
	private Long agenteId;
	@NotNull
	private Long polizaId;

}
