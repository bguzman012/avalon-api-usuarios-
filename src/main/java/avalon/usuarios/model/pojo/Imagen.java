package avalon.usuarios.model.pojo;

import avalon.usuarios.model.auditing.AuditingData;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "imagenes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Imagen extends AuditingData {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "documento")
    private byte[] documento;

    @Column(name = "nombre_docuemento")
    private String nombreDocumento;

    @Column(name = "topico")
    private String topico;

    @Temporal(TemporalType.DATE)
    private Date fechaInicio;

    @Temporal(TemporalType.DATE)
    private Date fechaFin;

}
