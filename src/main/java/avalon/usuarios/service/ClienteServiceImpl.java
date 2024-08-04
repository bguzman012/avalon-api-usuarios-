package avalon.usuarios.service;

import avalon.usuarios.data.ClienteRepository;
import avalon.usuarios.model.pojo.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClienteServiceImpl extends UsuariosServiceImpl<Cliente> implements ClienteService {

    private final String ROL_ADMIN = "ADM";
    private final String ROL_ASESOR = "ASR";
    private final String ROL_AGENTE = "BRO";

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
    public Page<Cliente> searchClientes(String estado, String busqueda, Pageable pageable, Usuario usuario) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Cliente> query = cb.createQuery(Cliente.class);
        Root<Cliente> root = null;
        Root<ClientePoliza> clientePolizaRoot = null;
        Join<ClientePoliza, Cliente> clienteJoin = null;

        if (usuario.getRol().getCodigo().equals(this.ROL_ADMIN)) {
            // Caso para rol ADMIN: Extraer todos los clientes
            root = query.from(Cliente.class);
        } else {
            // Caso para otros roles: Extraer clientes a través de ClientePoliza
            clientePolizaRoot = query.from(ClientePoliza.class);
            clienteJoin = clientePolizaRoot.join("cliente",  JoinType.RIGHT);
            query.select(clienteJoin).distinct(true);
        }

        List<Predicate> predicates = buildPredicates(cb, root, clientePolizaRoot, clienteJoin, estado, busqueda, usuario);

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        // Aplicar ordenación del pageable
        if (root != null) {
            Sort.Order sortOrder = pageable.getSort().iterator().next();
            Order order = sortOrder.isAscending() ? cb.asc(root.get(sortOrder.getProperty())) : cb.desc(root.get(sortOrder.getProperty()));
            query.orderBy(order);
        } else {
            Sort.Order sortOrder = pageable.getSort().iterator().next();
            Order order = sortOrder.isAscending() ? cb.asc(clienteJoin.get(sortOrder.getProperty())) : cb.desc(clienteJoin.get(sortOrder.getProperty()));
            query.orderBy(order);
        }

        List<Cliente> clientes = entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long totalRecords = countClientes(estado, busqueda, usuario);

        return new PageImpl<>(clientes, pageable, totalRecords);
    }

    private Long countClientes(String estado, String busqueda, Usuario usuario) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Cliente> root = null;
        Root<ClientePoliza> clientePolizaRoot = null;
        Join<ClientePoliza, Cliente> clienteJoin = null;

        if (usuario.getRol().getCodigo().equals(this.ROL_ADMIN)) {
            // Caso para rol ADMIN: Contar todos los clientes
            root = countQuery.from(Cliente.class);
        } else {
            // Caso para otros roles: Contar clientes a través de ClientePoliza
            clientePolizaRoot = countQuery.from(ClientePoliza.class);
            clienteJoin = clientePolizaRoot.join("cliente");
            countQuery.select(cb.countDistinct(clienteJoin));
        }

        List<Predicate> countPredicates = buildPredicates(cb, root, clientePolizaRoot, clienteJoin, estado, busqueda, usuario);

        if (root != null) {
            countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));
            countQuery.select(cb.count(root));
        } else {
            countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));
        }

        try {
            return entityManager.createQuery(countQuery).getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error en la consulta de conteo", e);
        }
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<Cliente> root, Root<ClientePoliza> clientePolizaRoot, Join<ClientePoliza, Cliente> clienteJoin, String estado, String busqueda, Usuario usuario) {
        List<Predicate> predicates = new ArrayList<>();

        if (usuario.getRol().getCodigo().equals(this.ROL_ADMIN)) {
            // Predicados para el rol ADMIN
            if (estado != null && !estado.isEmpty()) {
                predicates.add(cb.equal(root.get("estado"), estado));
            }

            if (busqueda != null && !busqueda.isEmpty()) {
                String likePattern = "%" + busqueda.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("nombres")), likePattern),
                        cb.like(cb.lower(root.get("nombresDos")), likePattern),
                        cb.like(cb.lower(root.get("apellidos")), likePattern),
                        cb.like(cb.lower(root.get("apellidosDos")), likePattern),
                        cb.like(cb.lower(root.get("correoElectronico")), likePattern),
                        cb.like(cb.lower(root.get("nombreUsuario")), likePattern)
                ));
            }
        } else {
            // Predicados para otros roles
            if (estado != null && !estado.isEmpty()) {
                predicates.add(cb.equal(clienteJoin.get("estado"), estado));
            }

            if (busqueda != null && !busqueda.isEmpty()) {
                String likePattern = "%" + busqueda.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(clienteJoin.get("nombres")), likePattern),
                        cb.like(cb.lower(clienteJoin.get("nombresDos")), likePattern),
                        cb.like(cb.lower(clienteJoin.get("apellidos")), likePattern),
                        cb.like(cb.lower(clienteJoin.get("apellidosDos")), likePattern),
                        cb.like(cb.lower(clienteJoin.get("correoElectronico")), likePattern),
                        cb.like(cb.lower(clienteJoin.get("nombreUsuario")), likePattern)
                ));
            }

            if (usuario.getRol().getCodigo().equals(this.ROL_ASESOR)) {
                predicates.add(cb.or(
                        cb.equal(clientePolizaRoot.get("asesor").get("id"), usuario.getId()),
                        cb.equal(clienteJoin.get("createdBy"), usuario.getNombreUsuario())
                ));
            } else if (usuario.getRol().getCodigo().equals(this.ROL_AGENTE)) {
                predicates.add(cb.or(
                        cb.equal(clientePolizaRoot.get("agente").get("id"), usuario.getId()),
                        cb.equal(clienteJoin.get("createdBy"), usuario.getNombreUsuario())
                ));
            }
        }

        return predicates;
    }

}
