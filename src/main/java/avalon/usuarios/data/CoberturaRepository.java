package avalon.usuarios.data;

import avalon.usuarios.model.pojo.Beneficio;
import avalon.usuarios.model.pojo.Cobertura;
import avalon.usuarios.model.pojo.Membresia;
import avalon.usuarios.model.pojo.Poliza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoberturaRepository extends JpaRepository<Cobertura, Long> {

    List<Cobertura> findAllByPoliza(Poliza poliza);
}
