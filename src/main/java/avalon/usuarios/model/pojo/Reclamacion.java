package avalon.usuarios.model.pojo;
import avalon.usuarios.model.auditing.AuditListener;
import avalon.usuarios.model.auditing.AuditingData;
import avalon.usuarios.service.ReclamacionService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "reclamaciones")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditListener.class)
public class Reclamacion extends AuditingData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", unique = true, updatable = false)
    private String codigo;

    @Temporal(TemporalType.DATE)
    private Date fechaServicio;

    @ManyToOne
    @JoinColumn(name = "medico_centro_medico_aseguradora_id")
    private MedicoCentroMedicoAseguradora medicoCentroMedicoAseguradora;

    @Column(name = "tipoAdm")
    @Enumerated(EnumType.STRING)
    private TipoAdm tipoAdm;

    @Column(name = "imagen_id")
    private Long imagenId;

    @Column(name = "padecimiento_diagnostico")
    private String padecimientoDiagnostico;

    @Column(name = "info_adicional")
    private String infoAdicional;

    @NotNull
    @Column(name = "estado")
    private String estado;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "cliente_poliza_id")
    private ClientePoliza clientePoliza;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "caso_id")
    private Caso caso;

    @JsonIgnore
    @OneToMany(mappedBy = "reclamacion", cascade = CascadeType.ALL)
    private List<Comentario> comentarioList;

}
