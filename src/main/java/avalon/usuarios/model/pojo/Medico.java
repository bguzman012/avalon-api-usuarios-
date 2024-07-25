package avalon.usuarios.model.pojo;

import avalon.usuarios.model.auditing.AuditingData;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "medicos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Medico extends AuditingData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "nombres")
    private String nombres;

    @Column(name = "nombres_dos")
    private String nombresDos;

    @NotNull
    @Column(name = "apellidos")
    private String apellidos;

    @Column(name = "apellidos_dos")
    private String apellidosDos;

    @NotNull
    @Column(name = "correo_electronico", unique = true)
    private String correoElectronico;

    @NotNull
    @Column(name = "numero_telefono")
    private String numeroTelefono;

    @Embedded
    private Direccion direccion;

    @NotNull
    @Column(name = "estado")
    private String estado;

    @Column(name = "url_imagen")
    private String urlImagen;

    @ManyToOne
    @NotNull(message = "Especialidad no puede ser nulo")
    @JoinColumn(name = "especialidad_id")
    private Especialidad especialidad;

}

