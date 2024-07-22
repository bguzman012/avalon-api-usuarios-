package avalon.usuarios.model.pojo;
import avalon.usuarios.model.auditing.AuditingData;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "citas_medicas")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CitaMedica extends AuditingData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", unique = true, updatable = false)
    private String codigo;

    @NotNull
    @Column(name = "razon")
    private String razon;

    @Column(name = "imagen_id")
    private Long imagenId;

    @NotNull
    @Column(name = "estado")
    private String estado;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "cliente_poliza_id")
    private ClientePoliza clientePoliza;

    @JsonIgnore
    @OneToMany(mappedBy = "citaMedica", cascade = CascadeType.ALL)
    private List<ComentarioCitasMedicas> comentarioCitasMedicasList;
}
