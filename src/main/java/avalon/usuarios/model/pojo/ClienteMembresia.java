package avalon.usuarios.model.pojo;

import avalon.usuarios.model.auditing.AuditingData;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;

@Entity
@Table(name = "clientes_membresias")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ClienteMembresia extends AuditingData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "membresia_id")
    private Membresia membresia;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "asesor_id")
    private Asesor asesor;

    @CreatedDate
    @Temporal(TemporalType.DATE)
    private Date fechaInicio;

    @CreatedDate
    @Temporal(TemporalType.DATE)
    private Date fechaFin;

    @NotNull
    @Column(name = "estado")
    private String estado;

}
