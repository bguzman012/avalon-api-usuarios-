package avalon.usuarios.model.pojo;

import avalon.usuarios.model.auditing.AuditListener;
import avalon.usuarios.model.auditing.AuditingData;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "brokers")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EntityListeners(AuditListener.class)
public class Broker extends AuditingData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "nombre")
    private String nombre;

    @NotNull
    @Column(name = "correo_electronico", unique = true)
    private String correoElectronico;

    @NotNull
    @Column(name = "estado")
    private String estado;

    @JsonIgnore
    @OneToMany(mappedBy = "broker", cascade = CascadeType.ALL)
    private List<Agente> agentes;

//    @ManyToOne
//    @JoinColumn(name = "tipo_aseguradora_id")
//    private TipoAseguradora tipoAseguradora;
//
//    @JsonIgnore
//    @OneToMany(mappedBy = "aseguradora", cascade = CascadeType.ALL)
//    private List<Membresia> membresiaList;
//
//    @JsonIgnore
//    @OneToMany(mappedBy = "aseguradora", cascade = CascadeType.ALL)
//    private List<Poliza> polizaList;
//
//    @JsonIgnore
//    @OneToMany(mappedBy = "aseguradora", cascade = CascadeType.ALL)
//    private List<UsuarioAseguradora> usuarioAseguradoraList;

}

