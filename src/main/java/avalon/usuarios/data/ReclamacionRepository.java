package avalon.usuarios.data;

import avalon.usuarios.model.pojo.Aseguradora;
import avalon.usuarios.model.pojo.ClientePoliza;
import avalon.usuarios.model.pojo.Comentario;
import avalon.usuarios.model.pojo.Reclamacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReclamacionRepository extends JpaRepository<Reclamacion, Long> {
    List<Reclamacion> findAllByEstado(String estado);

    @Query("SELECT r " +
            "FROM Reclamacion r WHERE r.clientePoliza = :clientePoliza")
    List<Reclamacion> findByClientePoliza(ClientePoliza clientePoliza);

}