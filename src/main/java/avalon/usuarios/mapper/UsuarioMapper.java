package avalon.usuarios.mapper;

import avalon.usuarios.model.pojo.*;
import avalon.usuarios.model.request.ClienteRequest;
import avalon.usuarios.model.request.UsuarioRequest;
import avalon.usuarios.service.EstadosService;
import avalon.usuarios.service.PaisService;
import avalon.usuarios.service.RolesService;
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

    @Autowired
    public UsuarioMapper(RolesService rolService) {
        this.rolService = rolService;
    }

    public <T extends Usuario> T mapToUsuario(UsuarioRequest request, T usuario) {
        String contrasenia = passwordEncoder.encode(request.getContrasenia());

        usuario.setNombres(request.getNombres());
        usuario.setNombresDos(request.getNombresDos());
        usuario.setApellidos(request.getApellidos());
        usuario.setApellidosDos(request.getApellidosDos());
        usuario.setCorreoElectronico(request.getCorreoElectronico());
        usuario.setNumeroTelefono(request.getNumeroTelefono());
        usuario.setNombreUsuario(request.getNombreUsuario());
        usuario.setContrasenia(contrasenia);
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

}
