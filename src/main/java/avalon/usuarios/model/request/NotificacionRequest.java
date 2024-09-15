package avalon.usuarios.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NotificacionRequest {

    @NotNull
    private String asunto;
    @NotNull
    private String mensaje;
    @NotNull
    private String usuarioEnvia;
    private String usuarioAseguradorId;
    @NotNull
    private Long tipoNotificacionId;

}
