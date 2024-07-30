package avalon.usuarios.model.pojo;

import avalon.usuarios.model.auditing.AuditListener;
import avalon.usuarios.model.auditing.AuditingData;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "polizas")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EntityListeners(AuditListener.class)
public class Poliza extends AuditingData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotNull
    @Column(name = "nombre")
    private String nombre;
    @NotNull
    @Column(name = "descripcion")
    private String descripcion;
    @NotNull
    @Column(name = "estado")
    private String estado;
    @Column(name = "vigencia_meses")
    private Long vigenciaMeses;
    @ManyToOne
    @NotNull
    @JoinColumn(name = "aseguradora_id")
    private Aseguradora aseguradora;

    @JsonIgnore
    @OneToMany(mappedBy = "poliza", cascade = CascadeType.ALL)
    private List<ClientePoliza> clientePolizaList;

    @JsonIgnore
    @OneToMany(mappedBy = "poliza", cascade = CascadeType.ALL)
    private List<Cobertura> coberturaList;

}

