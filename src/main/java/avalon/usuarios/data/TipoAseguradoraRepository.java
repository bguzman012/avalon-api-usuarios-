package avalon.usuarios.data;

import avalon.usuarios.model.pojo.Aseguradora;
import avalon.usuarios.model.pojo.TipoAseguradora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TipoAseguradoraRepository extends JpaRepository<TipoAseguradora, Long> {

}
