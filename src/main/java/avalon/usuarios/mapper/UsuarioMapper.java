package avalon.usuarios.mapper;

import avalon.usuarios.model.pojo.Cliente;
import avalon.usuarios.model.pojo.Usuario;
import avalon.usuarios.model.request.ClienteRequest;
import avalon.usuarios.model.request.UsuarioRequest;
import avalon.usuarios.service.RolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    private final RolesService rolService;

    @Autowired
    public UsuarioMapper(RolesService rolService) {
        this.rolService = rolService;
    }

    public <T extends Usuario> T mapToUsuario(UsuarioRequest request, T usuario) {
        usuario.setNombres(request.getNombres());
        usuario.setApellidos(request.getApellidos());
        usuario.setCorreoElectronico(request.getCorreoElectronico());
        usuario.setNumeroTelefono(request.getNumeroTelefono());
        usuario.setNombreUsuario(request.getNombreUsuario());
        usuario.setContrasenia(request.getContrasenia());
        usuario.setUrlImagen(request.getUrlImagen());
        usuario.setEstado(request.getEstado());
        usuario.setRol(rolService.findById(request.getRolId()));
        return usuario;
    }

    public Cliente mapToUsuario(ClienteRequest request, Cliente cliente) {
        mapToUsuario((UsuarioRequest) request, cliente);
        cliente.setFechaNacimiento(request.getFechaNacimiento());
        cliente.setLugarNacimiento(request.getLugarNacimiento());
        cliente.setLugarResidencia(request.getLugarResidencia());
        return cliente;
    }

}
