package avalon.usuarios.model.pojo;

import avalon.usuarios.model.auditing.AuditingData;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "centros_medicos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CentroMedico extends AuditingData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "nombre")
    private String nombre;

    @NotNull
    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "correo_electronico")
    private String correoElectronico;

    @Embedded
    private Direccion direccion;

    @JsonIgnore
    @OneToMany(mappedBy = "centroMedico", cascade = CascadeType.ALL)
    private List<MedicoCentroMedicoAseguradora> medicoCentroMedicoAseguradoraList;

}

