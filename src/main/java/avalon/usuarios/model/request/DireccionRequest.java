package avalon.usuarios.model.request;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DireccionRequest {

	@NotNull
	private String direccionUno;
	@NotNull
	private String codigoPostal;
	private String direccionDos;
	@NotNull
	private String ciudad;
	@NotNull
	private Long paisId;
	@NotNull
	private Long estadoId;
}
