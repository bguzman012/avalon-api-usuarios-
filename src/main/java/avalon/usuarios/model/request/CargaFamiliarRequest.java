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
public class CargaFamiliarRequest{
	@NotNull
	private String nombres;
	private String nombresDos;
	@NotNull
	private String apellidos;
	private String apellidosDos;
	@NotNull
	private String correoElectronico;
	@NotNull
	private String numeroTelefono;
	private String nombreUsuario;
	private String urlImagen;
	@NotNull
	private String estado;
	@NotNull
	private Long rolId;

	private Date fechaNacimiento;
	private String lugarNacimiento;
	private String lugarResidencia;
	private DireccionRequest direccion;

	private String numeroIdentificacion;
	private TipoIdentificacion tipoIdentificacion;

	@NotNull
	private String parentesco;
	private Long clienteId;
	@NotNull
	private String numeroCertificado;
	@NotNull
	private Long clientePolizaTitularId;

	private Long clienteMembresiaTitularId;
	private String codigoMembresia;
	// Getters and setters
}
