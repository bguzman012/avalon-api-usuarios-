package avalon.usuarios.data;

import avalon.usuarios.model.pojo.Aseguradora;
import avalon.usuarios.model.pojo.Broker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrokerRepository extends JpaRepository<Broker, Long> {

    List<Broker> findAllByEstado(String estado);

}
