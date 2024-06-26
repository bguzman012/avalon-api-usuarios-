package avalon.usuarios.model.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Cliente extends Usuario {

    @Temporal(TemporalType.DATE)
    private Date fechaNacimiento;

    @Column(name = "lugar_nacimiento")
    private String lugarNacimiento;

    @Column(name = "lugar_residencia")
    private String lugarResidencia;

    @JsonIgnore
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<ClientePoliza> clientePolizasList;

    @JsonIgnore
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<ClienteMembresia> clienteMembresiaList;


}

