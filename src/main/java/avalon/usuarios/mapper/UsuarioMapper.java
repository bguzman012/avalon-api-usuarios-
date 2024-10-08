package avalon.usuarios.mapper;

import avalon.usuarios.model.pojo.*;
import avalon.usuarios.model.request.CargaFamiliarRequest;
import avalon.usuarios.model.request.ClienteRequest;
import avalon.usuarios.model.request.MigracionClientePolizaRequest;
import avalon.usuarios.model.request.UsuarioRequest;
import avalon.usuarios.service.EstadosService;
import avalon.usuarios.service.PaisService;
import avalon.usuarios.service.RolesService;
import avalon.usuarios.service.mail.MailService;
import avalon.usuarios.util.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    private final RolesService rolService;
    @Autowired
    private PaisService paisService;
    @Autowired
    private EstadosService estadosService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private final Long ROL_CLIENTE = 3L;
    @Autowired
    private MailService mailService;

    @Autowired
    public UsuarioMapper(RolesService rolService) {
        this.rolService = rolService;
    }

    public <T extends Usuario> T mapToUsuario(UsuarioRequest request, T usuario) {
        String contrasenia = PasswordGenerator.generateTemporaryPassword();

        usuario.setContrasenia(passwordEncoder.encode(contrasenia));
        usuario.setContraseniaTemporalModificada(Boolean.FALSE);
        usuario.setNombres(request.getNombres());
        usuario.setNombresDos(request.getNombresDos());
        usuario.setApellidos(request.getApellidos());
        usuario.setApellidosDos(request.getApellidosDos());
        usuario.setCorreoElectronico(request.getCorreoElectronico());
        usuario.setNumeroTelefono(request.getNumeroTelefono());
        usuario.setNumeroIdentificacion(request.getNumeroIdentificacion());
        usuario.setTipoIdentificacion(request.getTipoIdentificacion());

        usuario.setUrlImagen(request.getUrlImagen());
        usuario.setEstado(request.getEstado());
        usuario.setRol(rolService.findById(request.getRolId()));
        return usuario;
    }

    public Cliente mapToUsuario(ClienteRequest request, Cliente cliente, Direccion direccion) {
        mapToUsuario((UsuarioRequest) request, cliente);
        Pais pais = paisService.findById(request.getDireccion().getPaisId()).orElseThrow(() -> new IllegalArgumentException("País no encontrado"));
        Estado estado = estadosService.findById(request.getDireccion().getEstadoId()).orElseThrow(() -> new IllegalArgumentException("Estado no encontrado"));

        cliente.setFechaNacimiento(request.getFechaNacimiento());
        cliente.setLugarNacimiento(request.getLugarNacimiento());
        cliente.setLugarResidencia(request.getLugarResidencia());

        direccion.setDireccionUno(request.getDireccion().getDireccionUno());
        direccion.setDireccionDos(request.getDireccion().getDireccionDos());
        direccion.setCodigoPostal(request.getDireccion().getCodigoPostal());
        direccion.setPais(pais);
        direccion.setState(estado);
        direccion.setCiudad(request.getDireccion().getCiudad());

        cliente.setDireccion(direccion);

        return cliente;
    }

    public Cliente mapToUsuarioFromCargaFamiliar(CargaFamiliarRequest request, Cliente cliente, Direccion direccion) {
        String contrasenia = PasswordGenerator.generateTemporaryPassword();

        cliente.setContrasenia(passwordEncoder.encode(contrasenia));
        cliente.setContraseniaTemporalModificada(Boolean.FALSE);
        cliente.setContraseniaTemporal(contrasenia);

        cliente.setNombres(request.getNombres());
        cliente.setNombresDos(request.getNombresDos());
        cliente.setApellidos(request.getApellidos());
        cliente.setApellidosDos(request.getApellidosDos());
        cliente.setCorreoElectronico(request.getCorreoElectronico());
        cliente.setNumeroTelefono(request.getNumeroTelefono());
        cliente.setNumeroIdentificacion(request.getNumeroIdentificacion());
        cliente.setTipoIdentificacion(request.getTipoIdentificacion());

        cliente.setUrlImagen(request.getUrlImagen());
        cliente.setEstado("A");
        cliente.setRol(rolService.findById(request.getRolId()));

        Pais pais = paisService.findById(request.getDireccion().getPaisId()).orElseThrow(() -> new IllegalArgumentException("País no encontrado"));
        Estado estado = estadosService.findById(request.getDireccion().getEstadoId()).orElseThrow(() -> new IllegalArgumentException("Estado no encontrado"));

        cliente.setFechaNacimiento(request.getFechaNacimiento());
        cliente.setLugarNacimiento(request.getLugarNacimiento());
        cliente.setLugarResidencia(request.getLugarResidencia());

        direccion.setDireccionUno(request.getDireccion().getDireccionUno());
        direccion.setDireccionDos(request.getDireccion().getDireccionDos());
        direccion.setCodigoPostal(request.getDireccion().getCodigoPostal());
        direccion.setPais(pais);
        direccion.setState(estado);
        direccion.setCiudad(request.getDireccion().getCiudad());

        cliente.setDireccion(direccion);

        return cliente;
    }

    public Cliente mapToClienteFromMigracionClientePoliza(MigracionClientePolizaRequest request, Cliente cliente, Direccion direccion) {
        String contrasenia = PasswordGenerator.generateTemporaryPassword();

        cliente.setContrasenia(passwordEncoder.encode(contrasenia));
        cliente.setContraseniaTemporalModificada(Boolean.FALSE);
        cliente.setContraseniaTemporal(contrasenia);

        cliente.setNombres(request.getNombres());
        cliente.setNombresDos(request.getNombresDos());
        cliente.setApellidos(request.getApellidos());
        cliente.setApellidosDos(request.getApellidosDos());
        cliente.setCorreoElectronico(request.getCorreoElectronico());
        cliente.setNumeroTelefono(request.getTelefono());
        cliente.setNumeroIdentificacion(request.getNumeroIdentificacion());
        cliente.setTipoIdentificacion(request.getTipoIdentificacion());

        cliente.setEstado("A");
        cliente.setRol(rolService.findById(this.ROL_CLIENTE));

        Pais pais = paisService.findByNombre(request.getPais()).orElseThrow(() -> new IllegalArgumentException("País no encontrado"));
        Estado estado = estadosService.findByNombre(request.getEstado()).orElseThrow(() -> new IllegalArgumentException("Estado no encontrado"));

        cliente.setFechaNacimiento(request.getFechaNacimiento());

        direccion.setDireccionUno(request.getDireccionUno());
        direccion.setDireccionDos(request.getDireccionDos());
        direccion.setCodigoPostal(request.getCodigoPostal());
        direccion.setPais(pais);
        direccion.setState(estado);
        direccion.setCiudad(request.getCiudad());

        cliente.setDireccion(direccion);
        return cliente;
    }


}
