package avalon.usuarios.service;

import avalon.usuarios.model.pojo.*;
import avalon.usuarios.model.request.ClientePolizaRequest;
import avalon.usuarios.model.request.MigracionClientePolizaRequest;
import avalon.usuarios.model.response.MigracionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ClientesPolizaService {

    String generarNuevoCodigo();

    Page<ClientePoliza> searchClienesPolizas(String busqueda, Pageable pageable, Cliente cliente, Poliza poliza, Usuario usuario);

    List<ClientePoliza> searchAllClienesPolizas(String busqueda, String sortField, String sortOrder);
    Boolean existClientePolizaTitular(String numeroCertificado, String tipo);

    ByteArrayOutputStream generateExcelClientesPolizas(String busqueda, String sortField, String sortOrder) throws IOException;

    //    List<ClientePoliza> exportClienesPolizas(String busqueda, Pageable pageable, Cliente cliente, Poliza poliza, Usuario usuario);
    Optional<ClientePoliza> getClientePoliza(Long clientePolizaId);
    Optional<ClientePoliza> getClientePolizaTitularByCertificado(String numeroCertificado);

    ClientePoliza savePoliza(ClientePoliza request);
    MigracionResponse saveMigracionClientePoliza(MigracionClientePolizaRequest request);

    void deleteClientePoliza(Long clientePolizaId);
}
