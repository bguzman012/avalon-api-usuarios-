package avalon.usuarios.data;

import avalon.usuarios.model.pojo.Aseguradora;
import avalon.usuarios.model.pojo.Beneficio;
import avalon.usuarios.model.pojo.Membresia;
import avalon.usuarios.model.pojo.Poliza;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PolizaRepository extends JpaRepository<Poliza, Long> {

    List<Poliza> findAllByAseguradora(Aseguradora aseguradora);
    Optional<Poliza> findByNombreContainingIgnoreCaseAndAseguradora(String nombre, Aseguradora aseguradora);

    @Query("SELECT p FROM Poliza p JOIN p.aseguradora a WHERE p.aseguradora = :aseguradora and " +
            "(:busqueda IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
            "LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
            "LOWER(a.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')))")
    Page<Poliza> searchPolizasByAseguradora(@Param("busqueda") String busqueda, @Param("aseguradora") Aseguradora aseguradora, Pageable pageable);

}
