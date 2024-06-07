package avalon.usuarios.data;

import avalon.usuarios.model.pojo.Membresia;
import avalon.usuarios.model.pojo.UsuarioAseguradora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioAseguradoraRepository extends JpaRepository<UsuarioAseguradora, Long> {

}
