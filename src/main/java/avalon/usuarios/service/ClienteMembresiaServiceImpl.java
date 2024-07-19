package avalon.usuarios.service;

import avalon.usuarios.data.*;
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
public class ClienteMembresiaServiceImpl implements ClienteMembresiaService {

    private final ClienteMembresiaRepository repository;
    @Autowired
    private ClienteService clienteService;
    @Autowired
    private MembresiaService membresiaService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public ClienteMembresiaServiceImpl(ClienteMembresiaRepository repository) {
        this.repository = repository;
    }

    @Override
    public ClienteMembresia saveClienteMembresia(ClienteMembresia clienteMembresia) {
        return repository.save(clienteMembresia);
    }

    public Page<ClienteMembresia> searchClientesMembresias(String busqueda, Pageable pageable, Cliente cliente, Membresia membresia) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Consulta principal para los resultados paginados
        CriteriaQuery<ClienteMembresia> query = cb.createQuery(ClienteMembresia.class);
        Root<ClienteMembresia> cmRoot = query.from(ClienteMembresia.class);
        Join<ClienteMembresia, Membresia> mJoin = cmRoot.join("membresia");
        Join<ClienteMembresia, Cliente> cJoin = cmRoot.join("cliente");
        Join<ClienteMembresia, Asesor> aJoin = cmRoot.join("asesor");

        List<Predicate> predicates = buildPredicates(cb, cmRoot, mJoin, cJoin, aJoin, busqueda, cliente, membresia);

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        Sort.Order sortOrder = pageable.getSort().iterator().next();
        Order order = sortOrder.isAscending() ? cb.asc(cmRoot.get(sortOrder.getProperty())) : cb.desc(cmRoot.get(sortOrder.getProperty()));
        query.orderBy(order);

        List<ClienteMembresia> resultList = entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long totalRecords = countClientesMembresias(busqueda, cliente, membresia);

        return new PageImpl<>(resultList, pageable, totalRecords);
    }

    private Long countClientesMembresias(String busqueda, Cliente cliente, Membresia membresia) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Subconsulta para el conteo total de registros
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<ClienteMembresia> countRoot = countQuery.from(ClienteMembresia.class);
        countQuery.select(cb.count(countRoot));

        Join<ClienteMembresia, Membresia> mJoin = countRoot.join("membresia");
        Join<ClienteMembresia, Cliente> cJoin = countRoot.join("cliente");
        Join<ClienteMembresia, Asesor> aJoin = countRoot.join("asesor");

        List<Predicate> countPredicates = buildPredicates(cb, countRoot, mJoin, cJoin, aJoin, busqueda, cliente, membresia);

        countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));

        try {
            return entityManager.createQuery(countQuery).getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error en la consulta de conteo", e);
        }
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<ClienteMembresia> cmRoot, Join<ClienteMembresia, Membresia> mJoin, Join<ClienteMembresia, Cliente> cJoin, Join<ClienteMembresia, Asesor> aJoin, String busqueda, Cliente cliente, Membresia membresia) {
        List<Predicate> predicates = new ArrayList<>();

        if (busqueda != null && !busqueda.isEmpty()) {
            String likePattern = "%" + busqueda.toLowerCase() + "%";

            predicates.add(cb.or(
                    cb.like(cb.lower(cJoin.get("nombreUsuario")), likePattern),
                    cb.like(cb.lower(mJoin.get("nombres")), likePattern),
                    cb.like(cb.lower(aJoin.get("nombreUsuario")), likePattern),
                    cb.like(cb.function("TO_CHAR", String.class, cmRoot.get("fechaInicio"), cb.literal("yyyy-MM-dd")), likePattern),
                    cb.like(cb.function("TO_CHAR", String.class, cmRoot.get("fechaFin"), cb.literal("yyyy-MM-dd")), likePattern)
            ));
        }

        if (cliente != null) {
            predicates.add(cb.equal(cmRoot.get("cliente"), cliente));
        }

        if (membresia != null) {
            predicates.add(cb.equal(cmRoot.get("membresia"), membresia));
        }

        return predicates;
    }

    @Override
    public Optional<ClienteMembresia> getClienteMembresia(Long id) {
        return this.repository.findById(id);
    }

    @Override
    public void deleteClienteMembresia(Long id) {
        repository.deleteById(id);
    }
}
