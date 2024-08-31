package avalon.usuarios.service;

import avalon.usuarios.data.EmpresaRepository;
import avalon.usuarios.model.pojo.Empresa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmpresaServiceImpl implements EmpresaService {

    private final EmpresaRepository repository;

    @Autowired
    public EmpresaServiceImpl(EmpresaRepository repository) {
        this.repository = repository;
    }


    @Override
    public Page<Empresa> searchEmpresas(String busqueda, Pageable pageable) {
        return repository.searchEmpresas(busqueda, pageable);
    }

    @Override
    public Optional<Empresa> getEmpresa(Long empresaId) {
        return repository.findById(empresaId);
    }

    @Override
    public Empresa saveEmpresa(Empresa empresa) {
        return repository.save(empresa);
    }

    @Override
    public void deleteEmpresa(Long empresaId) {
        repository.deleteById(empresaId);
    }

}
