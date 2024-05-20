package avalon.usuarios.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateRolRequest {

	private Long id;
	private String nombre;
	private String observaciones;

}
