package avalon.usuarios.data;

import avalon.usuarios.model.pojo.CentroMedico;
import avalon.usuarios.model.pojo.MedicoCentroMedicoAseguradora;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicoCentroMedicoAseguradoraRepository extends JpaRepository<MedicoCentroMedicoAseguradora, Long> {

}
