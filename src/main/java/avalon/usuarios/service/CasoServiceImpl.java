package avalon.usuarios.service;

import avalon.usuarios.data.CasoRepository;
import avalon.usuarios.model.pojo.Caso;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CasoServiceImpl implements CasoService {

    private final CasoRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public CasoServiceImpl(CasoRepository repository) {
        this.repository = repository;
    }


    @Override
    public Page<Caso> searchCasos(String busqueda, Pageable pageable, Long clientePolizaId) {
        return repository.searchCasos(busqueda, pageable, clientePolizaId);
    }

    @Override
    public Optional<Caso> getCaso(Long casoId) {
        return repository.findById(casoId);
    }

    @Override
    public Caso saveCaso(Caso caso) {
        if (caso.getCodigo() == null) {
            caso.setCodigo(this.generarNuevoCodigo());
        }
        return repository.save(caso);
    }

    @Override
    public void deleteCaso(Long casoId) {
        repository.deleteById(casoId);
    }

    public String generarNuevoCodigo() {
        try {
            String ultimoCodigo = (String) entityManager.createQuery("SELECT c.codigo FROM Caso c ORDER BY c.codigo DESC")
                    .setMaxResults(1)
                    .getSingleResult();

            int nuevoCodigoInt = Integer.parseInt(ultimoCodigo) + 1;
            return String.format("%07d", nuevoCodigoInt);
        } catch (NoResultException e) {
            // Si no hay resultados, se devuelve el primer código
            return "0000001";
        }
    }

}
