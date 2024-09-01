package avalon.usuarios.service;

import avalon.usuarios.data.AgenteRepository;
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
public class AgenteServiceImpl extends UsuariosServiceImpl<Agente> implements AgenteService {

    @Autowired
    private AgenteRepository agenteRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Agente> findAll(Pageable pageable) {
        return agenteRepository.findAll(pageable);
    }

    @Override
    public Page<Agente> findAllByEstado(String estado, Pageable pageable) {
        return agenteRepository.findAllByEstado(estado, pageable);
    }

    @Override
    public Page<Agente> searchAgentes(String estado, String busqueda, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Consulta principal para los resultados paginados
        CriteriaQuery<Agente> query = cb.createQuery(Agente.class);
        Root<Agente> root = query.from(Agente.class);

        List<Predicate> predicates = buildPredicates(cb, root, estado, busqueda);

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        // Aplicar ordenación del pageable
        Sort.Order sortOrder = pageable.getSort().iterator().next();
        Order order = sortOrder.isAscending() ? cb.asc(root.get(sortOrder.getProperty())) : cb.desc(root.get(sortOrder.getProperty()));
        query.orderBy(order);

        List<Agente> agentes = entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long totalRecords = countAgentees(estado, busqueda, null);

        return new PageImpl<>(agentes, pageable, totalRecords);
    }

    @Override
    public Optional<Agente> findByCorreo(String correo) {
        return agenteRepository.findByCorreoElectronico(correo);
    }

    @Override
    public Page<Agente> searchAgentesByBroker(String estado, String busqueda, Pageable pageable, Broker broker) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Consulta principal para los resultados paginados
        CriteriaQuery<Agente> query = cb.createQuery(Agente.class);
        Root<Agente> root = query.from(Agente.class);

        List<Predicate> predicates = buildPredicates(cb, root, estado, busqueda);

        if (broker != null) {
            predicates.add(cb.equal(root.get("broker"), broker));
        }

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        // Aplicar ordenación del pageable
        Sort.Order sortOrder = pageable.getSort().iterator().next();
        Order order = sortOrder.isAscending() ? cb.asc(root.get(sortOrder.getProperty())) : cb.desc(root.get(sortOrder.getProperty()));
        query.orderBy(order);

        List<Agente> agentes = entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long totalRecords = countAgentees(estado, busqueda, broker);

        return new PageImpl<>(agentes, pageable, totalRecords);
    }

    private Long countAgentees(String estado, String busqueda, Broker broker) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Subconsulta para el conteo total de registros
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Agente> countRoot = countQuery.from(Agente.class);
        countQuery.select(cb.count(countRoot));

        List<Predicate> countPredicates = buildPredicates(cb, countRoot, estado, busqueda);
        if (broker != null) {
            countPredicates.add(cb.equal(countRoot.get("broker"), broker));
        }

        countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));

        try {
            return entityManager.createQuery(countQuery).getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error en la consulta de conteo", e);
        }
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<Agente> root, String estado, String busqueda) {
        List<Predicate> predicates = new ArrayList<>();


        if (estado != null && !estado.isEmpty()) {
            predicates.add(cb.equal(root.get("estado"), estado));
        }

        if (busqueda != null && !busqueda.isEmpty()) {
            String likePattern = "%" + busqueda.toLowerCase() + "%";
            Predicate busquedaPredicado = cb.or(
                    cb.like(cb.lower(root.get("nombres")), likePattern),
                    cb.like(cb.lower(root.get("nombresDos")), likePattern),
                    cb.like(cb.lower(root.get("apellidos")), likePattern),
                    cb.like(cb.lower(root.get("apellidosDos")), likePattern),
                    cb.like(cb.lower(root.get("correoElectronico")), likePattern),
                    cb.like(cb.lower(root.get("nombreUsuario")), likePattern)
            );

            if (!predicates.isEmpty()) {
                predicates.add(cb.and(cb.equal(root.get("estado"), estado), busquedaPredicado));
            } else {
                predicates.add(busquedaPredicado);
            }
        }

        return predicates;
    }

}
