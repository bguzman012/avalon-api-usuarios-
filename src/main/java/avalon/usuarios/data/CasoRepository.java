package avalon.usuarios.data;

import avalon.usuarios.model.pojo.Caso;
import avalon.usuarios.model.pojo.Caso;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CasoRepository extends JpaRepository<Caso, Long> {

    @Query("SELECT c FROM Caso c WHERE " +
            "(:busqueda IS NULL OR LOWER(c.codigo) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
            " LOWER(c.codigo) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +

            " LOWER(c.clientePoliza.cliente.nombreUsuario) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
            " LOWER(c.clientePoliza.cliente.nombres) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
            " LOWER(c.clientePoliza.cliente.apellidos) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +

            " LOWER(c.clientePoliza.poliza.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%'))) AND " +
            "(:clientePolizaId IS NULL OR c.clientePoliza.id = :clientePolizaId)")
    Page<Caso> searchCasos(@Param("busqueda") String busqueda, Pageable pageable, @Param("clientePolizaId") Long clientePolizaId);

}
