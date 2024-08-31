package avalon.usuarios.model.pojo;

import avalon.usuarios.model.auditing.AuditListener;
import avalon.usuarios.model.auditing.AuditingData;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "clientes_polizas")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EntityListeners(AuditListener.class)
public class ClientePoliza extends AuditingData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "codigo", unique = true, updatable = false)
    private String codigo;

    @Column(name = "numeroCertificado")
    private String numeroCertificado;

    @ManyToOne
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "asesor_id")
    private Asesor asesor;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "agente_id")
    private Agente agente;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "poliza_id")
    private Poliza poliza;

    @Temporal(TemporalType.DATE)
    private Date fechaInicio;

    @Temporal(TemporalType.DATE)
    private Date fechaFin;

    @NotNull
    @Column(name = "estado")
    private String estado;

    @Column(name = "parentesco")
    private String parentesco;

    @Column(name = "tipo")
    private String tipo;

    @ManyToOne
    @JoinColumn(name = "titular_id")
    private ClientePoliza titular;

    @JsonIgnore
    @OneToMany(mappedBy = "clientePoliza", cascade = CascadeType.ALL)
    private List<CargaFamiliar> cargaFamiliarList;

    @JsonIgnore
    @OneToMany(mappedBy = "clientePoliza", cascade = CascadeType.ALL)
    private List<Reclamacion> reclamacionList;

    @JsonIgnore
    @OneToMany(mappedBy = "clientePoliza", cascade = CascadeType.ALL)
    private List<CitaMedica> citaMedicaList;

    @JsonIgnore
    @OneToMany(mappedBy = "clientePoliza", cascade = CascadeType.ALL)
    private List<Emergencia> emergenciaList;

    @JsonIgnore
    @OneToMany(mappedBy = "titular", cascade = CascadeType.ALL)
    private List<ClientePoliza> dependientes;

    @JsonIgnore
    @OneToMany(mappedBy = "clientePoliza", cascade = CascadeType.ALL)
    private List<Caso> casoList;

    // Campo no persistente para concatenar nombres y apellidos
    @Transient
    public String getDisplayName() {
        return (codigo != null ? codigo + " - " : "") +
                (poliza.getNombre() != null ? poliza.getNombre() : "") +
                (tipo != null ? " [" + tipo + "]" : "");
    }

}

