package avalon.usuarios.model.pojo;

import avalon.usuarios.model.auditing.AuditListener;
import avalon.usuarios.model.auditing.AuditingData;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "comentarios_emergencias")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditListener.class)
public class ComentarioEmergencia extends AuditingData {

    public ComentarioEmergencia(Long id, String contenido, Usuario usuarioComenta, String estado) {
        this.id = id;
        this.contenido = contenido;
        this.usuarioComenta = usuarioComenta;
        this.estado = estado;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "contenido")
    private String contenido;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "emergencia_id", nullable = false)
    private Emergencia emergencia;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "usuario_comenta_id", nullable = false)
    private Usuario usuarioComenta;

    @NotNull
    @Column(name = "estado")
    private String estado;
}
