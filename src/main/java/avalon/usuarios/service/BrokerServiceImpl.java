package avalon.usuarios.service;

import avalon.usuarios.data.*;
import avalon.usuarios.model.pojo.Broker;
import avalon.usuarios.model.request.BrokerRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrokerServiceImpl implements BrokerService {

    private final BrokerRepository repository;

//    @Autowired
//    private UsuarioAseguradoraRepository usuarioAseguradoraRepository;
//    @Autowired
//    private TipoAseguradoraRepository tipoAseguradoraRepository;
//    @Autowired
//    private UsuarioRepository usuarioRepository;

    @Autowired
    public BrokerServiceImpl(BrokerRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Broker> getBrokersByEstado(String estado) {
        return repository.findAllByEstado(estado);
    }

    @Override
    public Page<Broker> searchBrokers(String estado, String busqueda, Pageable pageable) {
        return repository.searchBrokers(estado, busqueda, pageable);
    }

    @Override
    public List<Broker> getBrokersByAgenteAndEstado(Long agenteId, String estado) {
        return null;
    }

    @Override
    public Broker getBroker(Long brokerId) {
        return repository.findById(brokerId).orElseThrow(() ->
                new IllegalArgumentException("Broker no encontrado"));
    }

    @Override
    public Broker createBroker(Broker broker) {
        return repository.save(broker);
    }

    @Override
    public Broker updateBroker(Broker broker, BrokerRequest request) {
        broker.setNombre(request.getNombre());
        broker.setCorreoElectronico(request.getCorreoElectronico());
        return this.createBroker(broker);
    }

    @Override
    public Broker partiallyUpdateBroker(String estado, Broker broker) {
        broker.setEstado(estado);
        return this.createBroker(broker);
    }

    @Override
    public void deleteBroker(Long brokerId) {
        repository.deleteById(brokerId);
    }

}
