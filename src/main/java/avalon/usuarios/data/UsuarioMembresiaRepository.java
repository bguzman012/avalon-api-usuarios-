package avalon.usuarios.data;

import avalon.usuarios.model.pojo.UsuarioAseguradora;
import avalon.usuarios.model.pojo.UsuarioMembresia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioMembresiaRepository extends JpaRepository<UsuarioMembresia, Long> {

}
