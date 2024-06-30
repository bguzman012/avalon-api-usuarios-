package avalon.usuarios.data;

import avalon.usuarios.model.pojo.Estado;
import avalon.usuarios.model.pojo.Pais;
import avalon.usuarios.model.pojo.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstadoRepository extends JpaRepository<Estado, Long> {

    List<Estado> findAllByPais(Pais pais);

}
