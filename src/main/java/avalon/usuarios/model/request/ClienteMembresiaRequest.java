package avalon.usuarios.model.request;

import jakarta.persistence.Column;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.util.Calendar;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClienteMembresiaRequest {

    @NotNull
    private Long membresiaId;
    @NotNull
    private Long clienteId;
    @NotNull
    private Long asesorId;
    @NotNull
    @Temporal(TemporalType.DATE)
    private Date fechaInicio;
    @NotNull
    @Temporal(TemporalType.DATE)
    private Date fechaFin;
}
