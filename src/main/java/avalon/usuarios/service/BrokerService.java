package avalon.usuarios.service;

import avalon.usuarios.model.pojo.Broker;
import avalon.usuarios.model.request.BrokerRequest;

import java.util.List;

public interface BrokerService {

    List<Broker> getBrokersByEstado(String estado);
    List<Broker> getBrokersByAgenteAndEstado(Long agenteId, String estado);
    Broker getBroker(Long brokerId);
    Broker createBroker(Broker broker);
    Broker updateBroker(Broker broker, BrokerRequest request);
    Broker partiallyUpdateBroker(String estado, Broker broker);
    void deleteBroker(Long brokerId);
}
