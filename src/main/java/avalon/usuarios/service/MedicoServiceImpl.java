package avalon.usuarios.service;

import avalon.usuarios.data.EspecialidadRepository;
import avalon.usuarios.data.MedicoRepository;
import avalon.usuarios.model.pojo.Medico;
import avalon.usuarios.model.pojo.ClientePoliza;
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
public class MedicoServiceImpl implements MedicoService {

    private final MedicoRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public MedicoServiceImpl(MedicoRepository repository) {
        this.repository = repository;
    }


    @Override
    public Medico save(Medico medico) {
        return this.repository.save(medico);
    }

    @Override
    public Optional<Medico> findById(Long id) {
        return this.repository.findById(id);
    }

    @Override
    public Page<Medico> searchMedicos(String busqueda, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Consulta principal para los resultados paginados
        CriteriaQuery<Medico> query = cb.createQuery(Medico.class);
        Root<Medico> root = query.from(Medico.class);

        List<Predicate> predicates = buildPredicates(cb, root, busqueda);

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        // Aplicar ordenación del pageable
        Sort.Order sortOrder = pageable.getSort().iterator().next();
        Order order = sortOrder.isAscending() ? cb.asc(root.get(sortOrder.getProperty())) : cb.desc(root.get(sortOrder.getProperty()));
        query.orderBy(order);

        List<Medico> medicos = entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long totalRecords = countMedicos(busqueda);

        return new PageImpl<>(medicos, pageable, totalRecords);
    }

    @Override
    public void deleteById(Long id) {
        this.repository.deleteById(id);
    }

    private Long countMedicos(String busqueda) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Subconsulta para el conteo total de registros
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Medico> countRoot = countQuery.from(Medico.class);
        countQuery.select(cb.count(countRoot));

        List<Predicate> countPredicates = buildPredicates(cb, countRoot, busqueda);

        countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));

        try {
            return entityManager.createQuery(countQuery).getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error en la consulta de conteo", e);
        }
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<Medico> root, String busqueda) {
        List<Predicate> predicates = new ArrayList<>();

        if (busqueda != null && !busqueda.isEmpty()) {
            String likePattern = "%" + busqueda.toLowerCase() + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("nombres")), likePattern),
                    cb.like(cb.lower(root.get("nombresDos")), likePattern),
                    cb.like(cb.lower(root.get("apellidos")), likePattern),
                    cb.like(cb.lower(root.get("apellidosDos")), likePattern),
                    cb.like(cb.lower(root.get("correoElectronico")), likePattern),
                    cb.like(cb.lower(root.get("especialidad").get("nombre")), likePattern),
                    cb.like(cb.lower(root.get("especialidad").get("descripcion")), likePattern)
            ));
        }


        return predicates;
    }

}
