package avalon.usuarios.data;

import avalon.usuarios.model.pojo.Agente;
import avalon.usuarios.model.pojo.Cliente;
import avalon.usuarios.model.pojo.ClientePoliza;
import avalon.usuarios.model.pojo.Poliza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientePolizaRepository extends JpaRepository<ClientePoliza, Long> {

    List<ClientePoliza> findAllByPoliza(Poliza poliza);
    List<ClientePoliza> findAllByCliente(Cliente cliente);

}
