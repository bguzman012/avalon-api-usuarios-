package avalon.usuarios.model.pojo;

import avalon.usuarios.model.auditing.AuditingData;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "medico_centr_med_aseg",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"aseguradora_id", "medico_id", "centro_medico_id"})
        })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class MedicoCentroMedicoAseguradora extends AuditingData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "aseguradora_id")
    private Aseguradora aseguradora;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "medico_id")
    private Medico medico;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "centro_medico_id")
    private CentroMedico centroMedico;

    @JsonIgnore
    @OneToMany(mappedBy = "medicoCentroMedicoAseguradora", cascade = CascadeType.ALL)
    private List<CitaMedica> citaMedicaList;

}

