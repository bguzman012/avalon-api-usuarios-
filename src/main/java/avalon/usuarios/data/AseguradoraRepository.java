package avalon.usuarios.data;

import avalon.usuarios.model.pojo.Aseguradora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AseguradoraRepository extends JpaRepository<Aseguradora, Long> {

    List<Aseguradora> findAllByEstadoAndTipoAseguradora(String estado, TipoAseguradora tipoAseguradora);

}
