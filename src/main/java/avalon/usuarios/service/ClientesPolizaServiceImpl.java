package avalon.usuarios.service;

import avalon.usuarios.data.AseguradoraRepository;
import avalon.usuarios.data.ClientePolizaRepository;
import avalon.usuarios.data.PolizaRepository;
import avalon.usuarios.data.UsuarioRepository;
import avalon.usuarios.model.pojo.Aseguradora;
import avalon.usuarios.model.pojo.ClientePoliza;
import avalon.usuarios.model.pojo.Poliza;
import avalon.usuarios.model.pojo.Usuario;
import avalon.usuarios.model.request.CreateClientePolizaRequest;
import avalon.usuarios.model.request.CreatePolizaRequest;
import avalon.usuarios.model.request.UpdateClientePolizaRequest;
import avalon.usuarios.model.request.UpdatePolizaRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientesPolizaServiceImpl implements ClientesPolizaService {

    @Autowired
    private PolizaRepository polizaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
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
    public ClientePoliza getClientePoliza(Long clientePolizaId) {
        return this.repository.findById(clientePolizaId).orElse(null);
    }

    @Override
    public ClientePoliza createClientePoliza(CreateClientePolizaRequest request) {
        Usuario cliente = this.usuarioRepository.findById(request.getClienteId()).orElse(null);
        Usuario asesor = this.usuarioRepository.findById(request.getAsesorId()).orElse(null);
        Usuario agente = this.usuarioRepository.findById(request.getAgenteId()).orElse(null);
        Poliza poliza = this.polizaRepository.findById(request.getPolizaId()).orElse(null);

        if (cliente == null || asesor == null || agente == null || poliza == null) return null;

        ClientePoliza clientePoliza = new ClientePoliza();
        clientePoliza.setCliente(cliente);
        clientePoliza.setAsesor(asesor);
        clientePoliza.setAgente(agente);
        clientePoliza.setPoliza(poliza);
        clientePoliza.setEstado("A");
        return this.repository.save(clientePoliza);
    }

    @Override
    public ClientePoliza updateClientePoliza(ClientePoliza clientePoliza, UpdateClientePolizaRequest request) {
        Usuario cliente = this.usuarioRepository.findById(request.getClienteId()).orElse(null);
        Usuario asesor = this.usuarioRepository.findById(request.getAsesorId()).orElse(null);
        Usuario agente = this.usuarioRepository.findById(request.getAgenteId()).orElse(null);
        Poliza poliza = this.polizaRepository.findById(request.getPolizaId()).orElse(null);

        if (cliente == null || asesor == null || agente == null || poliza == null) return null;

        clientePoliza.setCliente(cliente);
        clientePoliza.setAsesor(asesor);
        clientePoliza.setAgente(agente);
        clientePoliza.setPoliza(poliza);
        clientePoliza.setEstado(clientePoliza.getEstado());
        return this.repository.save(clientePoliza);

    }

    @Override
    public void deleteClientePoliza(Long clientePolizaId) {
        ClientePoliza clientePoliza = this.getClientePoliza(clientePolizaId);
        this.repository.delete(clientePoliza);

    }
}
