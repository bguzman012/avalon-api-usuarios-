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

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ClienteServiceImpl extends UsuariosServiceImpl<Cliente> implements ClienteService {

    private final String ROL_ADMIN = "ADM";
    private final String ROL_ASESOR = "ASR";
    private final String ROL_AGENTE = "BRO";
    private final String ROL_CLIENTE = "CLI";

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

        // subquerys cliente
        Subquery<Long> subqueryDependientes = null;
        Subquery<Long> subqueryTitular = null;


        if (usuario.getRol().getCodigo().equals(this.ROL_ADMIN)) {
            // Caso para rol ADMIN: Extraer todos los clientes
            root = query.from(Cliente.class);
        }

        if (usuario.getRol().getCodigo().equals(this.ROL_AGENTE) || usuario.getRol().getCodigo().equals(this.ROL_ASESOR)) {
            // Caso para otros roles: Extraer clientes a través de ClientePoliza
            clientePolizaRoot = query.from(ClientePoliza.class);
            clienteJoin = clientePolizaRoot.join("cliente", JoinType.RIGHT);
            query.select(clienteJoin).distinct(true);
        }

        if (usuario.getRol().getCodigo().equals(this.ROL_CLIENTE)) {
            LocalDate today = LocalDate.now();
            LocalDate eighteenYearsAgo = today.minusYears(18);
            Date fechaLimite = Date.from(eighteenYearsAgo.atStartOfDay(ZoneId.systemDefault()).toInstant());

            root = query.from(Cliente.class);

            // Subconsulta para obtener los dependientes del cliente
            subqueryDependientes = query.subquery(Long.class);
            Root<ClientePoliza> subqueryClientePolizaTitular = subqueryDependientes.from(ClientePoliza.class);
            Join<ClientePoliza, ClientePoliza> subqueryDependientePolizaJoin = subqueryClientePolizaTitular.join("dependientes");
            Join<ClientePoliza, Cliente> subqueryClienteDependienteJoin = subqueryDependientePolizaJoin.join("cliente");
            subqueryDependientes.select(subqueryClienteDependienteJoin.get("id")).distinct(true)
                    .where(cb.equal(subqueryClientePolizaTitular.get("cliente").get("id"), usuario.getId()),
                            cb.greaterThan(subqueryClienteDependienteJoin.get("fechaNacimiento"), fechaLimite));

            // Subconsulta para obtener el cliente titular
            subqueryTitular = query.subquery(Long.class);
            Root<Cliente> subqueryTitularRoot = subqueryTitular.from(Cliente.class);
            subqueryTitular.select(subqueryTitularRoot.get("id"))
                    .where(cb.equal(subqueryTitularRoot.get("id"), usuario.getId()));
        }

        List<Predicate> predicates = buildPredicates(cb, root, clientePolizaRoot, clienteJoin, estado, busqueda, usuario);


        if (usuario.getRol().getCodigo().equals(this.ROL_CLIENTE)) {
            Predicate titularOrDependientesPredicate = cb.or(
                    root.get("id").in(subqueryDependientes),
                    root.get("id").in(subqueryTitular)
            );

            predicates.add(titularOrDependientesPredicate);
        }

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        // Aplicar ordenación del pageable
        if (root != null) {
            Sort.Order sortOrder = pageable.getSort().iterator().next();
            Order order = sortOrder.isAscending() ? cb.asc(root.get(sortOrder.getProperty())) : cb.desc(root.get(sortOrder.getProperty()));
            query.orderBy(order);
        }

        if (usuario.getRol().getCodigo().equals(this.ROL_ASESOR) || usuario.getRol().getCodigo().equals(this.ROL_AGENTE)) {
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

        // subquerys cliente
        Subquery<Long> subqueryDependientes = null;
        Subquery<Long> subqueryTitular = null;

        if (usuario.getRol().getCodigo().equals(this.ROL_ADMIN)) {
            // Caso para rol ADMIN: Contar todos los clientes
            root = countQuery.from(Cliente.class);
        }

        if (usuario.getRol().getCodigo().equals(this.ROL_AGENTE) || usuario.getRol().getCodigo().equals(this.ROL_ASESOR)) {
            // Caso para otros roles: Contar clientes a través de ClientePoliza
            clientePolizaRoot = countQuery.from(ClientePoliza.class);
            clienteJoin = clientePolizaRoot.join("cliente", JoinType.RIGHT);
            countQuery.select(cb.countDistinct(clienteJoin));
        }

        if (usuario.getRol().getCodigo().equals(this.ROL_CLIENTE)) {
            LocalDate today = LocalDate.now();
            LocalDate eighteenYearsAgo = today.minusYears(18);
            Date fechaLimite = Date.from(eighteenYearsAgo.atStartOfDay(ZoneId.systemDefault()).toInstant());

            root = countQuery.from(Cliente.class);

            // Subconsulta para obtener los dependientes del cliente
            subqueryDependientes = countQuery.subquery(Long.class);
            Root<ClientePoliza> subqueryClientePolizaTitular = subqueryDependientes.from(ClientePoliza.class);
            Join<ClientePoliza, ClientePoliza> subqueryDependientePolizaJoin = subqueryClientePolizaTitular.join("dependientes");
            Join<ClientePoliza, Cliente> subqueryClienteDependienteJoin = subqueryDependientePolizaJoin.join("cliente");
            subqueryDependientes.select(subqueryClienteDependienteJoin.get("id")).distinct(true)
                    .where(cb.equal(subqueryClientePolizaTitular.get("cliente").get("id"), usuario.getId()),
                            cb.greaterThan(subqueryClienteDependienteJoin.get("fechaNacimiento"), fechaLimite));

            // Subconsulta para obtener el cliente titular
            subqueryTitular = countQuery.subquery(Long.class);
            Root<Cliente> subqueryTitularRoot = subqueryTitular.from(Cliente.class);
            subqueryTitular.select(subqueryTitularRoot.get("id"))
                    .where(cb.equal(subqueryTitularRoot.get("id"), usuario.getId()));

        }

        List<Predicate> countPredicates = buildPredicates(cb, root, clientePolizaRoot, clienteJoin, estado, busqueda, usuario);

        if (usuario.getRol().getCodigo().equals(this.ROL_CLIENTE)) {
            Predicate titularOrDependientesPredicate = cb.or(
                    root.get("id").in(subqueryDependientes),
                    root.get("id").in(subqueryTitular)
            );

            countPredicates.add(titularOrDependientesPredicate);
        }

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

        if (usuario.getRol().getCodigo().equals(this.ROL_ADMIN) || usuario.getRol().getCodigo().equals(this.ROL_CLIENTE) ) {
            // Predicados para el rol ADMIN

            if (estado != null && !estado.isBlank() && usuario.getRol().getCodigo().equals(this.ROL_ADMIN))
                predicates.add(cb.equal(root.get("estado"), estado));

            if (!usuario.getRol().getCodigo().equals(this.ROL_ADMIN))
                predicates.add(cb.equal(root.get("estado"), "A"));

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
