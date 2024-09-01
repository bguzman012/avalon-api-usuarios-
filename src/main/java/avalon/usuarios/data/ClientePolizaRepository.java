package avalon.usuarios.data;

import avalon.usuarios.model.pojo.Agente;
import avalon.usuarios.model.pojo.Cliente;
import avalon.usuarios.model.pojo.ClientePoliza;
import avalon.usuarios.model.pojo.Poliza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientePolizaRepository extends JpaRepository<ClientePoliza, Long> {

    List<ClientePoliza> findAllByPoliza(Poliza poliza);
    List<ClientePoliza> findAllByCliente(Cliente cliente);
    List<ClientePoliza> findAllByEstado(String estado);
    Optional<ClientePoliza> findByNumeroCertificadoAndTipo(String numeroCertificado, String tipoPoliza);
    Boolean existsByNumeroCertificadoAndTipoAndEstado(String numeroCertificado, String tipoPoliza, String estado);

}
