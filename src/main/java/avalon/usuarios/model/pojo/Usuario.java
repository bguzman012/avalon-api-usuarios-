package avalon.usuarios.model.pojo;

import avalon.usuarios.model.auditing.AuditingData;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Usuario extends AuditingData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "usuario", unique = true)
    private String usuario;

    @NotNull
    @Column(name = "contrasenia")
    private String contrasenia;

    @NotNull
    @Column(name = "correo_electronico", unique = true)
    private String correoElectronico;

    @NotNull
    @Column(name = "estado")
    private String estado;

    @NotNull
    @Temporal(TemporalType.DATE)
    private Date fechaNacimiento;

    @NotNull
    @Column(name = "nombres")
    private String nombres;

    @NotNull
    @Column(name = "apellidos")
    private String apellidos;

    @NotNull
    @Column(name = "telefono")
    private String telefono;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "rol_id")
    private Rol rol;

    @Column(name = "referencia_personal")
    private String referenciaPersonal;

    @Column(name = "direccion")
    private String direccion;

}

