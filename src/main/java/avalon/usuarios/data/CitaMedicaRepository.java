package avalon.usuarios.data;

import avalon.usuarios.model.pojo.ClientePoliza;
import avalon.usuarios.model.pojo.CitaMedica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CitaMedicaRepository extends JpaRepository<CitaMedica, Long> {
    List<CitaMedica> findAllByEstado(String estado);

    @Query("SELECT c FROM CitaMedica c WHERE c.clientePoliza = :clientePoliza")
    List<CitaMedica> findByClientePoliza(ClientePoliza clientePoliza);
}
