package avalon.usuarios.data;

import avalon.usuarios.model.pojo.Caso;
import avalon.usuarios.model.pojo.CitaMedica;
import avalon.usuarios.model.pojo.ClientePoliza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CasoRepository extends JpaRepository<Caso, Long> {
    List<Caso> findAllByEstado(String estado);

    @Query("SELECT c FROM Caso c WHERE c.clientePoliza = :clientePoliza")
    List<Caso> findByClientePoliza(ClientePoliza clientePoliza);
}
