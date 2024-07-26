package avalon.usuarios.service;

import avalon.usuarios.data.MedicoCentroMedicoAseguradoraRepository;
import avalon.usuarios.model.pojo.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MedicoCentroMedicoAseguradoraServiceImpl implements MedicoCentroMedicoAseguradoraService {

    private final MedicoCentroMedicoAseguradoraRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public MedicoCentroMedicoAseguradoraServiceImpl(MedicoCentroMedicoAseguradoraRepository repository) {
        this.repository = repository;
    }

    @Override
    public MedicoCentroMedicoAseguradora saveMedicoCentroMedicoAseguradora(MedicoCentroMedicoAseguradora clienteMembresia) {
        return repository.save(clienteMembresia);
    }

    public Page<MedicoCentroMedicoAseguradora> searchMedicoCentroMedicoAseguradoras(String busqueda,
                                                                                    Pageable pageable,
                                                                                    Medico medico,
                                                                                    Aseguradora aseguradora,
                                                                                    CentroMedico centroMedico) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Consulta principal para los resultados paginados
        CriteriaQuery<MedicoCentroMedicoAseguradora> query = cb.createQuery(MedicoCentroMedicoAseguradora.class);
        Root<MedicoCentroMedicoAseguradora> cmRoot = query.from(MedicoCentroMedicoAseguradora.class);

        List<Predicate> predicates = buildPredicates(cb, cmRoot, busqueda, medico, centroMedico, aseguradora);

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        Sort.Order sortOrder = pageable.getSort().iterator().next();
        Order order = sortOrder.isAscending() ? cb.asc(cmRoot.get(sortOrder.getProperty())) : cb.desc(cmRoot.get(sortOrder.getProperty()));
        query.orderBy(order);

        List<MedicoCentroMedicoAseguradora> resultList = entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long totalRecords = countClientesMembresias(busqueda, medico, aseguradora, centroMedico);

        return new PageImpl<>(resultList, pageable, totalRecords);
    }

    private Long countClientesMembresias(String busqueda,
                                         Medico medico,
                                         Aseguradora aseguradora,
                                         CentroMedico centroMedico) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Subconsulta para el conteo total de registros
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<MedicoCentroMedicoAseguradora> countRoot = countQuery.from(MedicoCentroMedicoAseguradora.class);
        countQuery.select(cb.count(countRoot));

        List<Predicate> countPredicates = buildPredicates(cb, countRoot, busqueda, medico, centroMedico, aseguradora);

        countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));

        try {
            return entityManager.createQuery(countQuery).getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error en la consulta de conteo", e);
        }
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb,
                                            Root<MedicoCentroMedicoAseguradora> cmRoot,
                                            String busqueda,
                                            Medico medico,
                                            CentroMedico centroMedico,
                                            Aseguradora aseguradora) {
        List<Predicate> predicates = new ArrayList<>();

        if (busqueda != null && !busqueda.isEmpty()) {
            String likePattern = "%" + busqueda.toLowerCase() + "%";

            predicates.add(cb.or(
                    cb.like(cb.lower(cmRoot.get("aseguradora").get("nombre")), likePattern),

                    cb.like(cb.lower(cmRoot.get("medico").get("nombres")), likePattern),
                    cb.like(cb.lower(cmRoot.get("medico").get("nombresDos")), likePattern),
                    cb.like(cb.lower(cmRoot.get("medico").get("apellidos")), likePattern),
                    cb.like(cb.lower(cmRoot.get("medico").get("apellidosDos")), likePattern),

                    cb.like(cb.lower(cmRoot.get("centroMedico").get("nombre")), likePattern)
            ));
        }

        if (medico != null) {
            predicates.add(cb.equal(cmRoot.get("medico"), medico));
        }

        if (aseguradora != null) {
            predicates.add(cb.equal(cmRoot.get("aseguradora"), aseguradora));
        }

        if (centroMedico != null) {
            predicates.add(cb.equal(cmRoot.get("centroMedico"), centroMedico));
        }

        return predicates;
    }

    @Override
    public Optional<MedicoCentroMedicoAseguradora> getMedicoCentroMedicoAseguradora(Long id) {
        return this.repository.findById(id);
    }

    @Override
    public void deleteMedicoCentroMedicoAseguradora(Long id) {
        repository.deleteById(id);
    }
}
