package avalon.usuarios.model.request;

import avalon.usuarios.model.pojo.RequisitoAdicional;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CitaMedicaRequest {

	@NotNull
	@Temporal(TemporalType.DATE)
	private Date fechaTentativa;
	private String ciudadPreferencia;
	@NotNull
	private String padecimiento;
	@NotNull
	private String informacionAdicional;
	private String estado;
	private String nombreDocumento;
	@NotNull
	private Long clientePolizaId;
	private Long medicoCentroMedicoAseguradoraId;
	private Map<RequisitoAdicional, Boolean> requisitosAdicionales;
	private String otrosRequisitos;
	@NotNull
	private Long casoId;
}