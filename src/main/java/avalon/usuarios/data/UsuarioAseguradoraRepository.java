package avalon.usuarios.data;

import avalon.usuarios.model.pojo.Aseguradora;
import avalon.usuarios.model.pojo.Membresia;
import avalon.usuarios.model.pojo.Usuario;
import avalon.usuarios.model.pojo.UsuarioAseguradora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioAseguradoraRepository extends JpaRepository<UsuarioAseguradora, Long> {

    UsuarioAseguradora findByAseguradoraAndUsuario(Aseguradora aseguradora, Usuario usuario);
    List<UsuarioAseguradora> findByUsuarioAndEstado(Usuario usuario, String estado);

}
