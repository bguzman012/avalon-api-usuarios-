package avalon.usuarios.model.request;

import avalon.usuarios.model.pojo.ComentarioEmergencia;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PartiallyUpdateEmergenciasRequest {

	@NotNull
	private String estado;
	private ComentarioEmergenciaRequest comentarioEmergenciaRequest;

}