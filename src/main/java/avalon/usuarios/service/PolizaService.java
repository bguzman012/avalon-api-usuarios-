package avalon.usuarios.service;

import avalon.usuarios.model.pojo.Poliza;

import java.util.List;
import java.util.Optional;

public interface PolizaService {

    List<Poliza> getPolizas();
    List<Poliza> getPolizasByAseguradora(Long aseguradoraId);
    Optional<Poliza> getPoliza(Long polizaId);
    Poliza savePoliza(Poliza poliza);
    void deletePoliza(Long polizaId);
}
