package avalon.usuarios.data;

import avalon.usuarios.model.pojo.Aseguradora;
import avalon.usuarios.model.pojo.ClientePoliza;
import avalon.usuarios.model.pojo.Reclamacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReclamacionRepository extends JpaRepository<Reclamacion, Long> {
    List<Reclamacion> findAllByEstado(String estado);

    @Query("SELECT new avalon.usuarios.model.pojo.Reclamacion(r.id, r.razon, r.estado, r.clientePoliza) " +
            "FROM Reclamacion r WHERE r.clientePoliza = :clientePoliza")
    List<Reclamacion> findByClientePoliza(ClientePoliza clientePoliza);

    @Query("SELECT new avalon.usuarios.model.pojo.Reclamacion(r.id, r.razon, r.estado, r.clientePoliza) " +
            "FROM Reclamacion r")
    List<Reclamacion> findAll();

}