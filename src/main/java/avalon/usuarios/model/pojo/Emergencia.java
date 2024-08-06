package avalon.usuarios.model.pojo;
import avalon.usuarios.model.auditing.AuditListener;
import avalon.usuarios.model.auditing.AuditingData;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "emergencias")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditListener.class)
public class Emergencia extends AuditingData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", unique = true, updatable = false)
    private String codigo;

    @Column(name = "imagen_id")
    private Long imagenId;

    @NotNull
    @Column(name = "estado")
    private String estado;

    @Column(name = "diagnostico")
    private String diagnostico;

    @Column(name = "sintomas")
    private String sintomas;

    @Embedded
    private Direccion direccion;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "cliente_poliza_id")
    private ClientePoliza clientePoliza;

    @ManyToOne
    @JoinColumn(name = "medico_centro_medico_aseguradora_id")
    private MedicoCentroMedicoAseguradora medicoCentroMedicoAseguradora;

    @JsonIgnore
    @OneToMany(mappedBy = "emergencia", cascade = CascadeType.ALL)
    private List<ComentarioEmergencia> comentarioEmergenciaList;
}
