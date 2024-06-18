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
public class UpdatePolizaRequest {

	@NotNull
	private String nombre;
	@NotNull
	private String descripcion;
	private Long vigenciaMeses;
	private Long aseguradoraId;
}
