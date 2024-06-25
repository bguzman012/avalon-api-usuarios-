package avalon.usuarios.model.pojo;

import avalon.usuarios.model.auditing.AuditingData;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.JOINED)
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
    @Column(name = "nombres")
    private String nombres;

    @NotNull
    @Column(name = "apellidos")
    private String apellidos;

    @NotNull
    @Column(name = "correo_electronico", unique = true)
    private String correoElectronico;

    @NotNull
    @Column(name = "numero_telefono")
    private String numeroTelefono;

    @NotNull
    @Column(name = "nombre_usuario", unique = true)
    private String nombreUsuario;

    @NotNull
    @Column(name = "contrasenia")
    private String contrasenia;

    @Column(name = "url_imagen")
    private String urlImagen;

    @NotNull
    @Column(name = "estado")
    private String estado;

    @ManyToOne
    @NotNull(message = "Rol no puede ser nulo")
    @JoinColumn(name = "rol_id")
    private Rol rol;

    @JsonIgnore
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<ClienteMembresia> clienteMembresiaList;

    @JsonIgnore
    @OneToMany(mappedBy = "asesor", cascade = CascadeType.ALL)
    private List<ClienteMembresia> asesorMembresiaList;

}

