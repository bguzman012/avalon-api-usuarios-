package avalon.usuarios.data;

import avalon.usuarios.model.pojo.CargaFamiliar;
import avalon.usuarios.model.pojo.Cliente;
import avalon.usuarios.model.pojo.ClientePoliza;
import avalon.usuarios.model.pojo.Poliza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CargaFamiliarRepository extends JpaRepository<CargaFamiliar, Long> {

    List<CargaFamiliar> findAllByClientePoliza(ClientePoliza clientePoliza);

}
