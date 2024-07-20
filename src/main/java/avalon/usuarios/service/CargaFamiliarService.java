package avalon.usuarios.service;

import avalon.usuarios.model.pojo.Asesor;
import avalon.usuarios.model.pojo.CargaFamiliar;
import avalon.usuarios.model.pojo.Cliente;
import avalon.usuarios.model.pojo.ClientePoliza;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CargaFamiliarService {
    CargaFamiliar save(CargaFamiliar entity);
    Optional<CargaFamiliar> findById(Long id);
    List<CargaFamiliar> findAll();
    Page<CargaFamiliar> searchCargasByClientePoliza(String busqueda, ClientePoliza clientePoliza, Pageable pageable);
    List<CargaFamiliar> findAllByClientePoliza(ClientePoliza clientePoliza);
    void deleteById(Long id);
}