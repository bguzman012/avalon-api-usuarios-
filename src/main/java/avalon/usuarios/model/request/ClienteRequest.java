package avalon.usuarios.model.request;

import avalon.usuarios.model.pojo.Usuario;
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
public class ClienteRequest extends UsuarioRequest {

	private Date fechaNacimiento;
	private String lugarNacimiento;
	private String lugarResidencia;

}
