package avalon.usuarios.model.pojo;

import avalon.usuarios.model.auditing.AuditingData;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "clientes_polizas")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ClientePoliza extends AuditingData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "cliente_id")
    private Usuario cliente;

    @ManyToOne
    @JoinColumn(name = "asesor_id")
    private Usuario asesor;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "agente_id")
    private Usuario agente;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "poliza_id")
    private Poliza poliza;

    @NotNull
    @Column(name = "estado")
    private String estado;

}

