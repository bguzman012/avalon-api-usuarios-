package avalon.usuarios.data;

import avalon.usuarios.model.pojo.Rol;
import avalon.usuarios.model.pojo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BaseUsuarioRepository <T extends Usuario> extends JpaRepository<T, Long> {

    List<T> findAllByEstado(String estado);

}
