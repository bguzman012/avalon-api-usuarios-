package avalon.usuarios.model.pojo;
import avalon.usuarios.model.auditing.AuditListener;
import avalon.usuarios.model.auditing.AuditingData;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Entity
@Table(name = "citas_medicas")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditListener.class)
public class CitaMedica extends AuditingData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", unique = true, updatable = false)
    private String codigo;

    @Column(name = "ciudad_preferencia")
    private String ciudadPreferencia;

    @Column(name = "padecimiento")
    private String padecimiento;

    @Column(name = "informacion_adicional")
    private String informacionAdicional;

    @Column(name = "otros_requisitos")
    private String otrosRequisitos;

    @Temporal(TemporalType.DATE)
    private Date fechaTentativa;

    @Column(name = "imagen_id")
    private Long imagenId;

    @NotNull
    @Column(name = "estado")
    private String estado;

    @ManyToOne
//    @NotNull
    @JoinColumn(name = "medico_centro_medico_aseguradora_id")
    private MedicoCentroMedicoAseguradora medicoCentroMedicoAseguradora;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "cliente_poliza_id")
    private ClientePoliza clientePoliza;

    @JsonIgnore
    @OneToMany(mappedBy = "citaMedica", cascade = CascadeType.ALL)
    private List<ComentarioCitasMedicas> comentarioCitasMedicasList;

    @ElementCollection
    @CollectionTable(name = "cita_medica_requisitos_adicionales", joinColumns = @JoinColumn(name = "cita_medica_id"))
    @MapKeyEnumerated(EnumType.STRING)
    @Column(name = "requisitosAdicionales")
    private Map<RequisitoAdicional, Boolean> requisitosAdicionales = new HashMap<>();

}
