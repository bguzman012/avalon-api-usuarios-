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
public class MedicoCentroMedicoAseguradoraRequest {

    @NotNull
    private Long aseguradoraId;
    @NotNull
    private Long medicoId;
    @NotNull
    private Long centroMedicoId;

}
