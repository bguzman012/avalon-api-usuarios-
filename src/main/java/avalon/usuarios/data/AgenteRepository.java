package avalon.usuarios.data;

import avalon.usuarios.model.pojo.Agente;
import avalon.usuarios.model.pojo.Asesor;
import avalon.usuarios.model.pojo.Rol;
import avalon.usuarios.model.pojo.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("agenteRepository")
public interface AgenteRepository extends BaseUsuarioRepository<Agente> {

    Page<Agente> findAll(Pageable pageable);
    Page<Agente> findAllByEstado(String estado, Pageable pageable);
    Optional<Agente> findByCorreoElectronico(String correoElectronico);


}
