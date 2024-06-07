package avalon.usuarios.data;

import avalon.usuarios.model.pojo.Membresia;
import avalon.usuarios.model.pojo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembresiaRepository extends JpaRepository<Membresia, Long> {

}
