package avalon.usuarios.service;

import avalon.usuarios.data.AgenteRepository;
import avalon.usuarios.data.AsesorRepository;
import avalon.usuarios.data.BaseUsuarioRepository;
import avalon.usuarios.data.ClienteRepository;
import avalon.usuarios.model.pojo.Asesor;
import avalon.usuarios.model.pojo.Cliente;
import avalon.usuarios.model.pojo.Usuario;
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
import java.util.Optional;

@Service
public class AsesorServiceImpl extends UsuariosServiceImpl<Asesor> implements AsesorService {

    @Autowired
    private AsesorRepository asesorRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Asesor> findAll(Pageable pageable) {
        return asesorRepository.findAll(pageable);
    }

    @Override
    public Page<Asesor> findAllByEstado(String estado, Pageable pageable) {
        return asesorRepository.findAllByEstado(estado, pageable);
    }

    @Override
    public Optional<Asesor> findByCorreo(String correo) {
        return asesorRepository.findByCorreoElectronico(correo);
    }

    @Override
    public Page<Asesor> searchAsesores(String estado, String busqueda, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Consulta principal para los resultados paginados
        CriteriaQuery<Asesor> query = cb.createQuery(Asesor.class);
        Root<Asesor> root = query.from(Asesor.class);

        List<Predicate> predicates = buildPredicates(cb, root, estado, busqueda);

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        // Aplicar ordenación del pageable
        Sort.Order sortOrder = pageable.getSort().iterator().next();
        Order order = sortOrder.isAscending() ? cb.asc(root.get(sortOrder.getProperty())) : cb.desc(root.get(sortOrder.getProperty()));
        query.orderBy(order);

        List<Asesor> asesores = entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long totalRecords = countAsesores(estado, busqueda);

        return new PageImpl<>(asesores, pageable, totalRecords);
    }

    private Long countAsesores(String estado, String busqueda) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Subconsulta para el conteo total de registros
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Asesor> countRoot = countQuery.from(Asesor.class);
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

    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<Asesor> root, String estado, String busqueda) {
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
