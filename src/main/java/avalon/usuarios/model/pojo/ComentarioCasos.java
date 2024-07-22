package avalon.usuarios.model.pojo;

import avalon.usuarios.model.auditing.AuditingData;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "comentarios_casos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ComentarioCasos extends AuditingData {

    public ComentarioCasos(Long id, String contenido, Usuario usuarioComenta, String estado) {
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
    @JoinColumn(name = "caso_id", nullable = false)
    private Caso caso;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "usuario_comenta_id", nullable = false)
    private Usuario usuarioComenta;

    @NotNull
    @Column(name = "estado")
    private String estado;
}
