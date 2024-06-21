package avalon.usuarios.model.pojo;

import avalon.usuarios.model.auditing.AuditingData;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "asesores")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Asesor extends Usuario {

    @JsonIgnore
    @OneToMany(mappedBy = "asesor", cascade = CascadeType.ALL)
    private List<ClientePoliza> asesorPolizasList;

    @JsonIgnore
    @OneToMany(mappedBy = "asesor", cascade = CascadeType.ALL)
    private List<ClienteMembresia> clienteMembresiaList;

}

