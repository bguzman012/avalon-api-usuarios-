package avalon.usuarios.service;

import avalon.usuarios.model.pojo.Broker;
import avalon.usuarios.model.request.BrokerRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BrokerService {

    List<Broker> getBrokersByEstado(String estado);
    Page<Broker> searchBrokers(String estado, String busqueda, Pageable pageable);
    List<Broker> getBrokersByAgenteAndEstado(Long agenteId, String estado);
    Broker getBroker(Long brokerId);
    Broker createBroker(Broker broker);
    Broker updateBroker(Broker broker, BrokerRequest request);
    Broker partiallyUpdateBroker(String estado, Broker broker);
    void deleteBroker(Long brokerId);
}
