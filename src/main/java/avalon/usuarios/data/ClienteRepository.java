package avalon.usuarios.data;

import avalon.usuarios.model.pojo.Asesor;
import avalon.usuarios.model.pojo.Cliente;
import avalon.usuarios.model.pojo.Rol;
import avalon.usuarios.model.pojo.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository("clienteRepository")
public interface ClienteRepository extends BaseUsuarioRepository<Cliente> {

    Page<Cliente> findAll(Pageable pageable);
    Page<Cliente> findAllByEstado(String estado, Pageable pageable);
    Optional<Cliente> findByCorreoElectronico(String correoElectronico);

}
