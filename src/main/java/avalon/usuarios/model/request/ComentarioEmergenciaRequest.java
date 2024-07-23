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
public class ComentarioEmergenciaRequest {

	@NotNull
	private Long emergenciaId;
	@NotNull
	private String contenido;
	@NotNull
	private Long usuarioComentaId;
	@NotNull
	private String estado;

}