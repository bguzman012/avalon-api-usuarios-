package avalon.usuarios.model.pojo;

import avalon.usuarios.model.auditing.AuditingData;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "usu_aseguradoras_usu_membresias")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UsuAseguradoraUsuMembresia extends AuditingData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "usuario_aseguradora_id")
    private UsuarioAseguradora usuarioAseguradora;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "usuario_membresia_id")
    private UsuarioMembresia usuarioMembresia;

}

