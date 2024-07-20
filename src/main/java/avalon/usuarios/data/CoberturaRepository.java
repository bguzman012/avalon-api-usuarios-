package avalon.usuarios.data;

import avalon.usuarios.model.pojo.Beneficio;
import avalon.usuarios.model.pojo.Cobertura;
import avalon.usuarios.model.pojo.Membresia;
import avalon.usuarios.model.pojo.Poliza;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoberturaRepository extends JpaRepository<Cobertura, Long> {

    List<Cobertura> findAllByPoliza(Poliza poliza);

    @Query("SELECT c FROM Cobertura c JOIN c.poliza p WHERE c.poliza = :poliza and " +
            "(:busqueda IS NULL OR LOWER(c.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
            "LOWER(c.descripcion) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
            "LOWER(c.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')))")
    Page<Cobertura> searchCoberturasByPoliza(@Param("busqueda") String busqueda, @Param("poliza") Poliza poliza, Pageable pageable);
}
