package avalon.usuarios.data;

import avalon.usuarios.model.pojo.UsuAseguradoraUsuMembresia;
import avalon.usuarios.model.pojo.UsuarioMembresia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuAseguradoraUsuMembresiaRepository extends JpaRepository<UsuAseguradoraUsuMembresia, Long> {

}
