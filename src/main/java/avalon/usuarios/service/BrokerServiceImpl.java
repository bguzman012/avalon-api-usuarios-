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


//
//    @Override
//    public List<CreateAseguradoraResponse> getAseguradoraByEstado(String estado, Long tipoEmpresaId) {
//        TipoAseguradora tipoAseguradora = this.tipoAseguradoraRepository.findById(tipoEmpresaId).orElse(null);
//
//        List <CreateAseguradoraResponse> createAseguradoraResponseList = new ArrayList<>();
//        for (Aseguradora aseg : repository.findAllByEstadoAndTipoAseguradora(estado, tipoAseguradora)
//        ) {
//            CreateAseguradoraResponse createAseguradoraResponse = new CreateAseguradoraResponse();
//            createAseguradoraResponse.setId(aseg.getId());
//            createAseguradoraResponse.setEstado(aseg.getEstado());
//            createAseguradoraResponse.setNombre(aseg.getNombre());
//            createAseguradoraResponse.setCorreoElectronico(aseg.getCorreoElectronico());
//            createAseguradoraResponseList.add(createAseguradoraResponse);
//        }
//        return createAseguradoraResponseList;
//    }
//
//    @Override
//    public List<CreateAseguradoraResponse> getAseguradoraByUsuarioAndEstado(Long usuarioId, String estado) {
//        List <CreateAseguradoraResponse> createAseguradoraResponseList = new ArrayList<>();
//
//        Usuario usuario = this.usuarioRepository.findById(usuarioId).orElse(null);
//
//        if (usuario  == null) return createAseguradoraResponseList;
//
//        List<UsuarioAseguradora> usuarioAseguradoraList = this.usuarioAseguradoraRepository.findByUsuarioAndEstado(usuario, "A");
//
//        for (UsuarioAseguradora usuarioAseguradora : usuarioAseguradoraList
//        ) {
//            CreateAseguradoraResponse createAseguradoraResponse = new CreateAseguradoraResponse();
//            createAseguradoraResponse.setId(usuarioAseguradora.getAseguradora().getId());
//            createAseguradoraResponse.setEstado(usuarioAseguradora.getAseguradora().getEstado());
//            createAseguradoraResponse.setNombre(usuarioAseguradora.getAseguradora().getNombre());
//            createAseguradoraResponse.setCorreoElectronico(usuarioAseguradora.getAseguradora().getCorreoElectronico());
//            createAseguradoraResponseList.add(createAseguradoraResponse);
//        }
//        return createAseguradoraResponseList;
//    }
//
//    @Override
//    public Aseguradora getAseguradora(Long aseguradoraId) {
//        return repository.findById(aseguradoraId).orElse(null);
//    }
//
//    @Override
//    public Aseguradora createAseguradora(CreateAseguradoraRequest request) {
//        TipoAseguradora tipoAseguradora = this.tipoAseguradoraRepository.findById(request.getTipoAseguradoraId()).orElse(null);
//
//        Aseguradora aseguradora = new Aseguradora();
//        aseguradora.setNombre(request.getNombre());
//        aseguradora.setCorreoElectronico(request.getCorreoElectronico());
//        aseguradora.setEstado("A");
//        aseguradora.setTipoAseguradora(tipoAseguradora);
//
//        return repository.save(aseguradora);
//    }
//
//    @Override
//    public Aseguradora updateAseguradora(Aseguradora aseguradora, UpdateAseguradoraRequest request) {
//        aseguradora.setNombre(request.getNombre());
//        aseguradora.setCorreoElectronico(request.getCorreoElectronico());
//        return repository.save(aseguradora);
//    }
//
//    @Override
//    public Aseguradora partiallyUpdateAseguradora(PartiallyUpdateAseguradora request, Long aseguradoraId) {
//        Aseguradora aseguradora = repository.findById(aseguradoraId).orElse(null);
//        if (aseguradora == null) return null;
//
//        if (request.getEstado() != null)
//            aseguradora.setEstado(request.getEstado());
//
//        return repository.save(aseguradora);
//    }
//
//    @Override
//    public void deleteAseguradora(Long aseguradoraId) {
//        repository.deleteById(aseguradoraId);
//    }

}
