package avalon.usuarios.model.request;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReclamacionRequest {

	private String estado;
	private String nombreDocumento;
	@NotNull
	private Long clientePolizaId;
	@NotNull
	@Temporal(TemporalType.DATE)
	private Date fechaServicio;
	@NotNull
	private Long medicoCentroMedicoAseguradoraId;
	@NotNull
	@Pattern(regexp = "EMERGENCIA|PROGRAMADA", message = "TipoAdm debe ser EMERGENCIA o PROGRAMADA")
	private String tipoAdm;
	@NotNull
	private String padecimientoDiagnostico;
	@NotNull
	private String infoAdicional;

}