package avalon.usuarios.data;

import avalon.usuarios.model.pojo.UsuAseguradoraUsuMembresia;
import avalon.usuarios.model.pojo.UsuarioMembresia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuAseguradoraUsuMembresiaRepository extends JpaRepository<UsuAseguradoraUsuMembresia, Long> {

    List<UsuAseguradoraUsuMembresia> findAllByUsuarioMembresia(UsuarioMembresia usuarioMembresia);

}
