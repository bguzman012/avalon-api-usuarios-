package avalon.usuarios.data;

import avalon.usuarios.model.pojo.Aseguradora;
import avalon.usuarios.model.pojo.Beneficio;
import avalon.usuarios.model.pojo.Membresia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BeneficioRepository extends JpaRepository<Beneficio, Long> {

    List<Beneficio> findAllByMembresia(Membresia membresia);

    @Query("SELECT b FROM Beneficio b JOIN b.membresia m WHERE b.membresia = :membresia and " +
            "(:busqueda IS NULL OR LOWER(b.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
            "LOWER(b.descripcion) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
            "LOWER(m.nombres) LIKE LOWER(CONCAT('%', :busqueda, '%')))")
    Page<Beneficio> searchBeneficiosByMembresia(@Param("busqueda") String busqueda,  @Param("membresia") Membresia membresia, Pageable pageable);
}
