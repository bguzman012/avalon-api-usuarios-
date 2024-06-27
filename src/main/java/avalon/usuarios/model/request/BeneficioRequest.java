package avalon.usuarios.model.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BeneficioRequest {

	@NotNull
	private String nombre;
	@NotNull
	private String descripcion;
	@NotNull
	private Long membresiaId;

}
