package avalon.usuarios.service;

import avalon.usuarios.model.pojo.Asesor;
import avalon.usuarios.model.pojo.CargaFamiliar;
import avalon.usuarios.model.pojo.ClientePoliza;

import java.util.List;
import java.util.Optional;

public interface CargaFamiliarService {
    CargaFamiliar save(CargaFamiliar entity);
    Optional<CargaFamiliar> findById(Long id);
    List<CargaFamiliar> findAll();
    List<CargaFamiliar> findAllByClientePoliza(ClientePoliza clientePoliza);
    void deleteById(Long id);
}