package avalon.usuarios.model.request;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClientePolizaRequest {

	@NotNull
	private Long clienteId;
	@NotNull
	private Long asesorId;
	@NotNull
	private Long agenteId;
	@NotNull
	private Long polizaId;
	@NotNull
	private String numeroCertificado;
	@NotNull
	@Temporal(TemporalType.DATE)
	private Date fechaInicio;
	@NotNull
	@Temporal(TemporalType.DATE)
	private Date fechaFin;

}
