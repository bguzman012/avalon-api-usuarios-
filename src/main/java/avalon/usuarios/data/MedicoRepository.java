package avalon.usuarios.data;

import avalon.usuarios.model.pojo.CargaFamiliar;
import avalon.usuarios.model.pojo.ClientePoliza;
import avalon.usuarios.model.pojo.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long> {

}
