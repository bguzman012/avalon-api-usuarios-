package avalon.usuarios.model.request;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateUsuarioRequest {

	private String usuario;
	private String contrasenia;
	private String correoElectronico;
	private String sobrenombre;
	private String estado;
	private String documentoIdentificacion;
	private String nombres;
	private String apellidos;
	private Date fechaNacimiento;
	private Double ubicacionLatitud;
	private Double ubicacionLongitud;
	private String telefono;
	private String urlImagen;
	private String urlFotoSenecytDoc;
	private String urlFotoRecordPolicial;
	private String referenciaPersonal;
	private String direccion;
	private Long idRol;

}
