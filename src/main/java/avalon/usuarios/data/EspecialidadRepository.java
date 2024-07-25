package avalon.usuarios.data;

import avalon.usuarios.model.pojo.Cobertura;
import avalon.usuarios.model.pojo.Especialidad;
import avalon.usuarios.model.pojo.Poliza;
import avalon.usuarios.model.pojo.Rol;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EspecialidadRepository extends JpaRepository<Especialidad, Long> {

    @Query("SELECT e FROM Especialidad e WHERE " +
            "(:busqueda IS NULL OR LOWER(e.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
            "LOWER(e.descripcion) LIKE LOWER(CONCAT('%', :busqueda, '%')))")
    Page<Especialidad> searchEspecialidades(@Param("busqueda") String busqueda, Pageable pageable);

}
