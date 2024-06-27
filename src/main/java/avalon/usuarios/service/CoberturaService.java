package avalon.usuarios.service;

import avalon.usuarios.model.pojo.Beneficio;
import avalon.usuarios.model.pojo.Cobertura;
import avalon.usuarios.model.pojo.Membresia;
import avalon.usuarios.model.pojo.Poliza;

import java.util.List;
import java.util.Optional;

public interface CoberturaService {

    List<Cobertura> getCoberturas();
    List<Cobertura> getCoberturasByPoliza(Poliza poliza);
    Optional<Cobertura> getCobertura(Long coberturaId);
    Cobertura saveCobertura(Cobertura cobertura);
    void deleteCobertura(Long coberturaId);
}
