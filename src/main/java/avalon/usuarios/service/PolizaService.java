package avalon.usuarios.service;

import avalon.usuarios.model.pojo.Aseguradora;
import avalon.usuarios.model.pojo.Beneficio;
import avalon.usuarios.model.pojo.Membresia;
import avalon.usuarios.model.pojo.Poliza;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PolizaService {

    List<Poliza> getPolizas();
    List<Poliza> getPolizasByAseguradora(Long aseguradoraId);
    Page<Poliza> searchPolizasByAseguradora(String busqueda, Pageable pageable, Aseguradora aseguradora);

    Optional<Poliza> getPoliza(Long polizaId);
    Optional<Poliza> getPolizaByNameAndAseguradora(String polizaName, Aseguradora aseguradora);
    Poliza savePoliza(Poliza poliza);
    void deletePoliza(Long polizaId);
}
