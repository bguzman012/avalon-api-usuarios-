package avalon.usuarios.model.pojo;

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
public class Asesor extends Usuario {

    @JsonIgnore
    @OneToMany(mappedBy = "asesor", cascade = CascadeType.ALL)
    private List<ClientePoliza> asesorPolizasList;

    @JsonIgnore
    @OneToMany(mappedBy = "asesor", cascade = CascadeType.ALL)
    private List<ClienteMembresia> clienteMembresiaList;

}

