package avalon.usuarios.model.pojo;

import avalon.usuarios.model.auditing.AuditListener;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "asesores")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EntityListeners(AuditListener.class)
public class Asesor extends Usuario {

    @JsonIgnore
    @OneToMany(mappedBy = "asesor", cascade = CascadeType.ALL)
    private List<ClientePoliza> asesorPolizasList;

    @JsonIgnore
    @OneToMany(mappedBy = "asesor", cascade = CascadeType.ALL)
    private List<ClienteMembresia> clienteMembresiaList;

}

