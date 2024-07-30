package avalon.usuarios.model.pojo;

import avalon.usuarios.model.auditing.AuditListener;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "paises")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EntityListeners(AuditListener.class)
public class Pais {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "nombre")
    private String nombre;

    @JsonIgnore
    @OneToMany(mappedBy = "pais", cascade = CascadeType.ALL)
    private List<Estado> estadoList;

}
