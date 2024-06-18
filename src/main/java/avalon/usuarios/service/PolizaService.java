package avalon.usuarios.service;

import avalon.usuarios.model.pojo.Membresia;
import avalon.usuarios.model.pojo.Poliza;
import avalon.usuarios.model.request.CreateMembresiaRequest;
import avalon.usuarios.model.request.CreatePolizaRequest;
import avalon.usuarios.model.request.UpdateMembresiaRequest;
import avalon.usuarios.model.request.UpdatePolizaRequest;

import java.util.List;

public interface PolizaService {

    List<Poliza> getPolizas();
    List<Poliza> getPolizasByAseguradora(Long aseguradoraId);
    Poliza getPoliza(Long polizaId);
    Poliza createPoliza(CreatePolizaRequest request);
    Poliza updatePoliza(Poliza poliza, UpdatePolizaRequest request);
    void deletePoliza(Long polizaId);
}
