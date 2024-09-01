package avalon.usuarios.data;

import avalon.usuarios.model.pojo.Pais;
import avalon.usuarios.model.pojo.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaisRepository extends JpaRepository<Pais, Long> {

    Optional<Pais> findByNombreContainingIgnoreCase(String nombre);

}
