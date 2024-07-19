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
public interface AseguradoraRepository extends JpaRepository<Aseguradora, Long> {

    List<Aseguradora> findAllByEstado(String estado);

    @Query("SELECT a FROM Aseguradora a WHERE (:estado IS NULL OR a.estado = :estado) AND " +
            "(:busqueda IS NULL OR LOWER(a.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
            "LOWER(a.correoElectronico) LIKE LOWER(CONCAT('%', :busqueda, '%')))")
    Page<Aseguradora> searchAseguradoras(@Param("estado") String estado, @Param("busqueda") String busqueda, Pageable pageable);

}
