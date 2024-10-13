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
public class EmergenciaRequest {

	private String estado;
	private Long medicoCentroMedicoAseguradoraId;
	private String nombreDocumento;
	private String tipoDocumento;
	private String diagnostico;
	private DireccionRequest direccion;
	@NotNull
	private String sintomas;
	@NotNull
	private Long clientePolizaId;
	@NotNull
	private Long casoId;

}