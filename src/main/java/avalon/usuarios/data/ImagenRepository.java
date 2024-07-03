package avalon.usuarios.data;

import avalon.usuarios.model.pojo.Imagen;
import avalon.usuarios.model.pojo.Membresia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.awt.*;
import java.util.List;
import java.util.Optional;

@Repository
public interface ImagenRepository extends JpaRepository<Imagen, Long> {

}
