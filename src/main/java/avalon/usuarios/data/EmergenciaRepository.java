package avalon.usuarios.data;

import avalon.usuarios.model.pojo.Emergencia;
import avalon.usuarios.model.pojo.ClientePoliza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmergenciaRepository extends JpaRepository<Emergencia, Long> {
    List<Emergencia> findAllByEstado(String estado);

    @Query("SELECT e FROM Emergencia e WHERE e.clientePoliza = :clientePoliza")
    List<Emergencia> findByClientePoliza(ClientePoliza clientePoliza);
}
