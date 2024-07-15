package avalon.usuarios.service;

import avalon.usuarios.data.ClienteRepository;
import avalon.usuarios.model.pojo.Asesor;
import avalon.usuarios.model.pojo.Cliente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClienteServiceImpl extends UsuariosServiceImpl<Cliente> implements ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Cliente> findAll(Pageable pageable) {
        return clienteRepository.findAll(pageable);
    }

    @Override
    public Page<Cliente> findAllByEstado(String estado, Pageable pageable) {
        return clienteRepository.findAllByEstado(estado, pageable);
    }

    @Override
    public Page<Cliente> searchClientes(String estado, String busqueda, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Consulta principal para los resultados paginados
        CriteriaQuery<Cliente> query = cb.createQuery(Cliente.class);
        Root<Cliente> root = query.from(Cliente.class);

        List<Predicate> predicates = buildPredicates(cb, root, estado, busqueda);

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        List<Cliente> clientes = entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long totalRecords = countClientes(estado, busqueda);

        return new PageImpl<>(clientes, pageable, totalRecords);
    }

    private Long countClientes(String estado, String busqueda) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Subconsulta para el conteo total de registros
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Cliente> countRoot = countQuery.from(Cliente.class);
        countQuery.select(cb.count(countRoot));

        List<Predicate> countPredicates = buildPredicates(cb, countRoot, estado, busqueda);

        countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));

        try {
            return entityManager.createQuery(countQuery).getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error en la consulta de conteo", e);
        }
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<Cliente> root, String estado, String busqueda) {
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
