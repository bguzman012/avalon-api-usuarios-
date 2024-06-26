package avalon.usuarios.service;

import avalon.usuarios.data.ClientePolizaRepository;
import avalon.usuarios.data.PolizaRepository;
import avalon.usuarios.data.UsuarioRepository;
import avalon.usuarios.model.pojo.ClientePoliza;
import avalon.usuarios.model.pojo.Poliza;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientesPolizaServiceImpl implements ClientesPolizaService {

    @Autowired
    private PolizaRepository polizaRepository;
    private final ClientePolizaRepository repository;

    @Autowired
    public ClientesPolizaServiceImpl(ClientePolizaRepository repository) {
        this.repository = repository;
    }


    @Override
    public List<ClientePoliza> getClientesPolizas() {
        return this.repository.findAll();
    }

    @Override
    public List<ClientePoliza> getClientesPolizasByPoliza(Long polizaId) {
        Poliza poliza = this.polizaRepository.findById(polizaId).orElse(null);

        if (poliza == null) return null;

        return this.repository.findAllByPoliza(poliza);
    }

    @Override
    public Optional<ClientePoliza> getClientePoliza(Long clientePolizaId) {
        return this.repository.findById(clientePolizaId);
    }

    @Override
    public ClientePoliza savePoliza(ClientePoliza clientePoliza) {
        return this.repository.save(clientePoliza);
    }

    @Override
    public void deleteClientePoliza(Long clientePolizaId) {
        ClientePoliza clientePoliza = this.getClientePoliza(clientePolizaId).orElseThrow(() -> new IllegalArgumentException("Cliente Poliza no encontrada"));;
        this.repository.delete(clientePoliza);
    }
}
