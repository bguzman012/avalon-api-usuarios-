package avalon.usuarios.data;

import avalon.usuarios.model.pojo.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClienteMembresiaRepository extends JpaRepository<ClienteMembresia, Long> {

    List<ClienteMembresia> findAllByMembresia(Membresia membresia);
    List<ClienteMembresia> findAllByCliente(Cliente cliente);

}
