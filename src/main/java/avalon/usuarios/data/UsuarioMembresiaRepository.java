package avalon.usuarios.data;

import avalon.usuarios.model.pojo.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioMembresiaRepository extends JpaRepository<UsuarioMembresia, Long> {

    List<UsuarioMembresia> findAllByMembresia(Membresia membresia);

}
