package avalon.usuarios.data;

import avalon.usuarios.model.pojo.Aseguradora;
import avalon.usuarios.model.pojo.CentroMedico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CentroMedicoRepository extends JpaRepository<CentroMedico, Long> {

    @Query("SELECT c FROM CentroMedico c WHERE " +
            "(:busqueda IS NULL OR LOWER(c.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
            ":busqueda IS NULL OR LOWER(c.descripcion) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
            "LOWER(c.correoElectronico) LIKE LOWER(CONCAT('%', :busqueda, '%')))")
    Page<CentroMedico> searchCentrosMedicos(@Param("busqueda") String busqueda, Pageable pageable);

}
