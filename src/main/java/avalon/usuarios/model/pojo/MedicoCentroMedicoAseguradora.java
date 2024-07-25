package avalon.usuarios.model.pojo;

import avalon.usuarios.model.auditing.AuditingData;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "medico_centr_med_aseg")
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

}

