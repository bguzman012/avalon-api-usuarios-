package avalon.usuarios.model.pojo;

import avalon.usuarios.model.auditing.AuditListener;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "estados")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EntityListeners(AuditListener.class)
public class Estado {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "nombre")
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "pais_id")
    private Pais pais;

}
