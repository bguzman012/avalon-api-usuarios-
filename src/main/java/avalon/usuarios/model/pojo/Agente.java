package avalon.usuarios.model.pojo;

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
@Builder
@ToString
public class Agente extends Usuario {

    @ManyToOne
    @NotNull
    @JoinColumn(name = "broker_id")
    private Broker broker;

    @JsonIgnore
    @OneToMany(mappedBy = "agente", cascade = CascadeType.ALL)
    private List<ClientePoliza> agentePolizasList;

}

