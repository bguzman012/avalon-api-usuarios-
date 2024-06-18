package avalon.usuarios.data;

import avalon.usuarios.model.pojo.Aseguradora;
import avalon.usuarios.model.pojo.Poliza;
import avalon.usuarios.model.pojo.TipoAseguradora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PolizaRepository extends JpaRepository<Poliza, Long> {

    List<Poliza> findAllByAseguradora(Aseguradora aseguradora);

}
