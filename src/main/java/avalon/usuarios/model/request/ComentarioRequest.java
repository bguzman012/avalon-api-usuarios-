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
public class ComentarioRequest {

	@NotNull
	private Long reclamacionId;
	@NotNull
	private String contenido;
	@NotNull
	private Long usuarioComentaId;
	@NotNull
	private String estado;

}