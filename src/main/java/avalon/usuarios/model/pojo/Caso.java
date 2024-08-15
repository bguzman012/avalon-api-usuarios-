package avalon.usuarios.model.pojo;

import avalon.usuarios.model.auditing.AuditListener;
import avalon.usuarios.model.auditing.AuditingData;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "casos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EntityListeners(AuditListener.class)
public class Caso extends AuditingData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "codigo", unique = true, updatable = false)
    private String codigo;

    @Column(name = "observaciones")
    private String observaciones;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "cliente_poliza_id")
    private ClientePoliza clientePoliza;

    @JsonIgnore
    @OneToMany(mappedBy = "caso", cascade = CascadeType.ALL)
    private List<CitaMedica> citaMedicaList;

    @JsonIgnore
    @OneToMany(mappedBy = "caso", cascade = CascadeType.ALL)
    private List<Reclamacion> reclamacionList;

    @JsonIgnore
    @OneToMany(mappedBy = "caso", cascade = CascadeType.ALL)
    private List<Emergencia> emergenciaList;

    // Campo no persistente para concatenar nombres y apellidos
    @Transient
    public String getDisplayName() {
        return (codigo != null ? "Caso - " + codigo : "");
    }

    @Transient
    public String getClienteDisplayName() {
        return (clientePoliza != null ? clientePoliza.getCliente().getNombreUsuario() + " [" + clientePoliza.getCliente().getNombres()
                + " " + clientePoliza.getCliente().getApellidos() + "]": "");
    }


}

