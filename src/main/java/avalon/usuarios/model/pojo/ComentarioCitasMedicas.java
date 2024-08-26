package avalon.usuarios.model.pojo;

import avalon.usuarios.model.auditing.AuditListener;
import avalon.usuarios.model.auditing.AuditingData;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "comentarios_citas_medicas")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditListener.class)
public class ComentarioCitasMedicas extends AuditingData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "contenido")
    private String contenido;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "cita_medica_id", nullable = false)
    private CitaMedica citaMedica;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "usuario_comenta_id", nullable = false)
    private Usuario usuarioComenta;

    @Column(name = "imagen_id")
    private Long imagenId;

    @NotNull
    @Column(name = "estado")
    private String estado;
}
