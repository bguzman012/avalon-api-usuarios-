package avalon.usuarios.model.request;

import avalon.usuarios.model.pojo.Aseguradora;
import jakarta.persistence.Column;
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
public class CreatePolizaRequest {

	@NotNull
	private String nombre;
	@NotNull
	private String descripcion;
	private Long vigenciaMeses;
	private Long aseguradoraId;
}
