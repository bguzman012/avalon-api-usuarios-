package avalon.usuarios.data;

import avalon.usuarios.model.pojo.Aseguradora;
import avalon.usuarios.model.pojo.Broker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrokerRepository extends JpaRepository<Broker, Long> {

    List<Broker> findAllByEstado(String estado);

    @Query("SELECT b FROM Broker b WHERE (:estado IS NULL OR b.estado = :estado) AND " +
            "(:busqueda IS NULL OR LOWER(b.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
            "LOWER(b.correoElectronico) LIKE LOWER(CONCAT('%', :busqueda, '%')))")
    Page<Broker> searchBrokers(@Param("estado") String estado, @Param("busqueda") String busqueda, Pageable pageable);


}
