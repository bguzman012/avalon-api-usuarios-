package avalon.usuarios.model.pojo;

import avalon.usuarios.model.auditing.AuditingData;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "membresias")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Membresia extends AuditingData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "nombres")
    private String nombres;

    @Column(name = "detalle")
    private String detalle;

    @NotNull
    @Column(name = "estado")
    private String estado;

    @Column(name = "vigencia_meses")
    private Long vigenciaMeses;

    @JsonIgnore
    @OneToMany(mappedBy = "membresia", cascade = CascadeType.ALL)
    private List<ClienteMembresia> clienteMembresiaList;

    @JsonIgnore
    @OneToMany(mappedBy = "membresia", cascade = CascadeType.ALL)
    private List<Beneficio> beneficioList;

}

