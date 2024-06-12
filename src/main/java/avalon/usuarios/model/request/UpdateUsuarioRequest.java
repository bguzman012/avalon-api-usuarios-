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
public class UpdateUsuarioRequest {

    @NotNull
    private String nombres;
    @NotNull
    private String apellidos;
    private Date fechaNacimiento;
    private Date lugarNacimiento;
    private Date lugarResidencia;
    @NotNull
    private String correoElectronico;
    @NotNull
    private String numeroTelefono;
    private String contrasenia;
    private String urlImagen;
    @NotNull
    private String estado;
    @NotNull
    private Long rolId;

}
