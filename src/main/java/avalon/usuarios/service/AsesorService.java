package avalon.usuarios.service;

import avalon.usuarios.model.pojo.Agente;
import avalon.usuarios.model.pojo.Asesor;
import avalon.usuarios.model.pojo.Cliente;
import avalon.usuarios.model.pojo.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface AsesorService extends UsuariosService<Asesor> {
    Page<Asesor> findAll(Pageable pageable);
    Page<Asesor> findAllByEstado(String estado, Pageable pageable);
    Optional<Asesor> findByCorreo(String correo);
    Page<Asesor> searchAsesores(String estado, String busqueda, Pageable pageable);
}