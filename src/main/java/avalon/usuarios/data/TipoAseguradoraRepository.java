package avalon.usuarios.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoAseguradoraRepository extends JpaRepository<TipoAseguradora, Long> {

}
