package avalon.usuarios.service;

import avalon.usuarios.data.AsesorRepository;
import avalon.usuarios.data.CargaFamiliarRepository;
import avalon.usuarios.model.pojo.Asesor;
import avalon.usuarios.model.pojo.ClientePoliza;
import avalon.usuarios.model.pojo.Cliente;
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
public class CargaFamiliarServiceImpl implements CargaFamiliarService {

    @Autowired
    private CargaFamiliarRepository repository;

    @PersistenceContext
    private EntityManager entityManager;
    

    @Override
    public Page<ClientePoliza> searchCargasByClientePoliza(String busqueda, ClientePoliza clientePoliza, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Consulta principal para los resultados paginados
        CriteriaQuery<ClientePoliza> query = cb.createQuery(ClientePoliza.class);
        Root<ClientePoliza> root = query.from(ClientePoliza.class);

        List<Predicate> predicates = buildPredicates(cb, root, busqueda, clientePoliza);

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        // Aplicar ordenación del pageable
        Sort.Order sortOrder = pageable.getSort().iterator().next();
        Order order = sortOrder.isAscending() ? cb.asc(root.get(sortOrder.getProperty())) : cb.desc(root.get(sortOrder.getProperty()));
        query.orderBy(order);

        List<ClientePoliza> clientePolizas = entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long totalRecords = countClientePolizas(busqueda, clientePoliza);

        return new PageImpl<>(clientePolizas, pageable, totalRecords);
    }

    private Long countClientePolizas(String busqueda, ClientePoliza clientePoliza) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Subconsulta para el conteo total de registros
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<ClientePoliza> countRoot = countQuery.from(ClientePoliza.class);
        countQuery.select(cb.count(countRoot));

        List<Predicate> countPredicates = buildPredicates(cb, countRoot, busqueda, clientePoliza);

        countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));

        try {
            return entityManager.createQuery(countQuery).getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error en la consulta de conteo", e);
        }
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<ClientePoliza> root, String busqueda, ClientePoliza clientePoliza) {
        List<Predicate> predicates = new ArrayList<>();

        if (busqueda != null && !busqueda.isEmpty()) {
            String likePattern = "%" + busqueda.toLowerCase() + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("cliente").get("nombres")), likePattern),
                    cb.like(cb.lower(root.get("cliente").get("nombresDos")), likePattern),
                    cb.like(cb.lower(root.get("cliente").get("apellidos")), likePattern),
                    cb.like(cb.lower(root.get("cliente").get("apellidosDos")), likePattern),
                    cb.like(cb.lower(root.get("cliente").get("correoElectronico")), likePattern),
                    cb.like(cb.lower(root.get("cliente").get("nombreUsuario")), likePattern),

                    cb.like(cb.lower(root.get("parentesco")), likePattern),
                    cb.like(cb.lower(root.get("numeroCertificado")), likePattern)
            ));
        }

        predicates.add(cb.equal(root.get("clientePoliza"), clientePoliza));


        return predicates;
    }


}
