package avalon.usuarios.service;

import avalon.usuarios.model.pojo.Empresa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface EmpresaService {

    Page<Empresa> searchEmpresas(String busqueda, Pageable pageable);
    Optional<Empresa> getEmpresa(Long empresaId);
    Optional<Empresa> getEmpresaByNombre(String empresaNombre);
    Empresa saveEmpresa(Empresa empresa);
    void deleteEmpresa(Long empresaId);
}
