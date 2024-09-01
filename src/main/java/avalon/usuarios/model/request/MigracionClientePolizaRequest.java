package avalon.usuarios.model.request;

import avalon.usuarios.model.pojo.TipoIdentificacion;
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
public class MigracionClientePolizaRequest {
	@NotNull
	private String seguro;
	private String empresa;

	@NotNull
	private String correoAsesor;
	@NotNull
	private String correoAgente;

	@NotNull
	private String nombres;
	private String nombresDos;
	@NotNull
	private String apellidos;
	private String apellidosDos;
	private String sexo;
	@NotNull
	private Date fechaNacimiento;
	@NotNull
	private String tipoPoliza;
	@NotNull
	private String parentesco;


	@NotNull
	private String direccionUno;
	private String codigoPostal;
	private String direccionDos;
	@NotNull
	private String ciudad;
	@NotNull
	private String pais;
	@NotNull
	private String estado;

	@NotNull
	private String correoElectronico;
	private String telefono;

	@NotNull
	private String poliza;
	@NotNull
	private String numeroCertificado;
	@NotNull
	private Date fechaInicioPoliza;
	@NotNull
	private Date fechaExpiracionPoliza;

	private String membresia;
	private String membresiaCodigo;
	private Date fechaInicioMembresia;
	private Date fechaExpiracionMembresia;

	private String numeroIdentificacion;
	private TipoIdentificacion tipoIdentificacion;

}
