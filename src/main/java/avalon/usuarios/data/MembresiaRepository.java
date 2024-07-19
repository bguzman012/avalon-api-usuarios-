package avalon.usuarios.data;

import avalon.usuarios.model.pojo.Aseguradora;
import avalon.usuarios.model.pojo.Beneficio;
import avalon.usuarios.model.pojo.Membresia;
import avalon.usuarios.model.pojo.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MembresiaRepository extends JpaRepository<Membresia, Long> {

    List<Membresia> findAllByEstado(String estado);

    @Query("SELECT m FROM Membresia m WHERE (:estado IS NULL OR m.estado = :estado) AND " +
            "(:busqueda IS NULL OR LOWER(m.nombres) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
            "LOWER(m.detalle) LIKE LOWER(CONCAT('%', :busqueda, '%')))")
    Page<Membresia> searchMembresias(@Param("estado") String estado, @Param("busqueda") String busqueda, Pageable pageable);


}
