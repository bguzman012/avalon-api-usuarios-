package avalon.usuarios.service;

import avalon.usuarios.model.pojo.MetodoPago;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MetodoPagoService {

    List<MetodoPago> searchMetodosPago();
    Optional<MetodoPago> getMetodoPago(Long aseguradoraId);
    MetodoPago saveMetodoPago(MetodoPago aseguradora);
    void deleteMetodoPago(Long aseguradoraId);
}
