package avalon.usuarios.data;

import avalon.usuarios.model.pojo.Empresa;
import avalon.usuarios.model.pojo.Empresa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    @Query("SELECT c FROM Empresa c WHERE " +
            "(:busqueda IS NULL OR LOWER(c.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
            ":busqueda IS NULL OR LOWER(c.descripcion) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
            "LOWER(c.correoElectronico) LIKE LOWER(CONCAT('%', :busqueda, '%')))")
    Page<Empresa> searchEmpresas(@Param("busqueda") String busqueda, Pageable pageable);

    Optional<Empresa> findByNombreContainingIgnoreCase(String nombre);

}
