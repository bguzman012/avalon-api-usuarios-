package avalon.usuarios.service;

import avalon.usuarios.model.pojo.Aseguradora;
import avalon.usuarios.model.pojo.Beneficio;
import avalon.usuarios.model.pojo.Membresia;
import avalon.usuarios.model.request.PartiallyUpdateAseguradora;

import java.util.List;
import java.util.Optional;

public interface BeneficioService {

    List<Beneficio> getBeneficios();
    List<Beneficio> getBeneficiosByMembresia(Membresia membresia);
    Optional<Beneficio> getBeneficio(Long beneficioId);
    Beneficio saveBeneficio(Beneficio beneficio);
    void deleteBeneficio(Long beneficioId);
}
