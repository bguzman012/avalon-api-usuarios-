package avalon.usuarios.model.pojo;

import avalon.usuarios.model.auditing.AuditingData;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "coberturas")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Cobertura extends AuditingData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "nombre")
    private String nombre;

    @NotNull
    @Column(name = "descripcion")
    private String descripcion;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "poliza_id")
    private Poliza poliza;

//    @JsonIgnore
//    @OneToMany(mappedBy = "aseguradora", cascade = CascadeType.ALL)
//    private List<Poliza> polizaList;
}

