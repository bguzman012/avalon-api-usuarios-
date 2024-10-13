package avalon.usuarios.model.pojo;

import avalon.usuarios.model.auditing.AuditListener;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EntityListeners(AuditListener.class)
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

    @Transient
    public String getDisplayName() {
        return (getNombreUsuario() != null ? getNombreUsuario() + " [" : "") +
                (getNombres() != null ? getNombres() + " " : "") +
                (getApellidos() != null ? getApellidos() + "]": "") ;
    }

    public boolean tiene18OMasAnios() {
        Calendar fechaActual = Calendar.getInstance();
        Calendar fechaNacimientoCal = Calendar.getInstance();
        fechaNacimientoCal.setTime(fechaNacimiento);

        int anioActual = fechaActual.get(Calendar.YEAR);
        int mesActual = fechaActual.get(Calendar.MONTH);
        int diaActual = fechaActual.get(Calendar.DAY_OF_MONTH);

        int anioNacimiento = fechaNacimientoCal.get(Calendar.YEAR);
        int mesNacimiento = fechaNacimientoCal.get(Calendar.MONTH);
        int diaNacimiento = fechaNacimientoCal.get(Calendar.DAY_OF_MONTH);

        int edad = anioActual - anioNacimiento;

        // Si no ha cumplido años este año, restar 1 a la edad
        if (mesActual < mesNacimiento || (mesActual == mesNacimiento && diaActual < diaNacimiento)) {
            edad--;
        }

        // Validar si el usuario tiene menos de 1 año (edad es 0)
        if (edad == 0) {
            return false; // El usuario tiene menos de 1 año
        }

        return edad >= 18;
    }

}

