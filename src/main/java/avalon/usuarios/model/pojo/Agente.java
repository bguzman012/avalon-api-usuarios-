package avalon.usuarios.model.pojo;

import avalon.usuarios.model.auditing.AuditListener;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "agentes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EntityListeners(AuditListener.class)
public class Agente extends Usuario {

    @ManyToOne
    @NotNull
    @JoinColumn(name = "broker_id")
    private Broker broker;

    @JsonIgnore
    @OneToMany(mappedBy = "agente", cascade = CascadeType.ALL)
    private List<ClientePoliza> agentePolizasList;

}

