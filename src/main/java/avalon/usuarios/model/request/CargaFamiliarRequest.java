package avalon.usuarios.model.request;

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
	private String nombres;
	private String nombresDos;
	private String apellidos;
	private String apellidosDos;
	private String correoElectronico;
	private String numeroTelefono;
	private String nombreUsuario;
	private String contrasenia;
	private String urlImagen;
	private String estado;
	private Long rolId;

	private Date fechaNacimiento;
	private String lugarNacimiento;
	private String lugarResidencia;
	private DireccionRequest direccion;

	@NotNull
	private String parentesco;
	private Long clienteId;
	@NotNull
	private String numeroCertificado;
	@NotNull
	private Long clientePolizaTitularId;
	// Getters and setters
}
