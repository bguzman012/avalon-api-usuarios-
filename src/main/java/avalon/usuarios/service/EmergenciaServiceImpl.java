package avalon.usuarios.service;

import avalon.usuarios.data.EmergenciaRepository;
import avalon.usuarios.model.pojo.Emergencia;
import avalon.usuarios.model.pojo.ClientePoliza;
import avalon.usuarios.model.request.PartiallyUpdateEmergenciasRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
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
public class EmergenciaServiceImpl implements EmergenciaService {

    private final EmergenciaRepository repository;
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public EmergenciaServiceImpl(EmergenciaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Emergencia> getEmergencia(Long casoId) {
        return repository.findById(casoId);
    }

    @Override
    public Emergencia saveEmergencia(Emergencia emergencia) {
        if (emergencia.getCodigo() == null) {
            emergencia.setCodigo(this.generarNuevoCodigo());
        }
        return repository.save(emergencia);
    }

    @Override
    public Emergencia partiallyUpdateEmergencia(PartiallyUpdateEmergenciasRequest request, Long casoId) {
        Emergencia emergencia = repository.findById(casoId).orElse(null);
        if (emergencia == null) return null;

        if (request.getEstado() != null)
            emergencia.setEstado(request.getEstado());

        return repository.save(emergencia);
    }

    @Override
    public void deleteEmergencia(Long casoId) {
        repository.deleteById(casoId);
    }

    @Override
    public String generarNuevoCodigo() {
        try {
            String ultimoCodigo = (String) entityManager.createQuery("SELECT e.codigo FROM Emergencia e ORDER BY e.codigo DESC")
                    .setMaxResults(1)
                    .getSingleResult();

            int nuevoCodigoInt = Integer.parseInt(ultimoCodigo) + 1;
            return String.format("%07d", nuevoCodigoInt);
        } catch (NoResultException e) {
            // Si no hay resultados, se devuelve el primer código
            return "0000001";
        }
    }

    public Page<Emergencia> searchEmergencias(String busqueda, String estado, Pageable pageable, ClientePoliza clientePoliza) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Consulta principal para los resultados paginados
        CriteriaQuery<Emergencia> query = cb.createQuery(Emergencia.class);
        Root<Emergencia> rRoot = query.from(Emergencia.class);
        Join<Emergencia, ClientePoliza> cpJoin = rRoot.join("clientePoliza");

        List<Predicate> predicates = buildPredicates(cb, rRoot, cpJoin, busqueda, estado, clientePoliza);

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        // Aplicar ordenación del pageable
        Sort.Order sortOrder = pageable.getSort().iterator().next();
        Order order = sortOrder.isAscending() ? cb.asc(rRoot.get(sortOrder.getProperty())) : cb.desc(rRoot.get(sortOrder.getProperty()));
        query.orderBy(order);

        List<Emergencia> resultList = entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long totalRecords = countEmergencias(busqueda, estado, clientePoliza);

        return new PageImpl<>(resultList, pageable, totalRecords);
    }

    private Long countEmergencias(String busqueda, String estado, ClientePoliza clientePoliza) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Subconsulta para el conteo total de registros
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Emergencia> countRoot = countQuery.from(Emergencia.class);
        countQuery.select(cb.count(countRoot));

        Join<Emergencia, ClientePoliza> cpJoin = countRoot.join("clientePoliza");

        List<Predicate> countPredicates = buildPredicates(cb, countRoot, cpJoin, busqueda, estado, clientePoliza);

        countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));

        try {
            return entityManager.createQuery(countQuery).getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error en la consulta de conteo", e);
        }
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<Emergencia> rRoot, Join<Emergencia, ClientePoliza> cpJoin, String busqueda, String estado, ClientePoliza clientePoliza) {
        List<Predicate> predicates = new ArrayList<>();

        if (busqueda != null && !busqueda.isEmpty()) {
            String likePattern = "%" + busqueda.toLowerCase() + "%";

            predicates.add(cb.or(
                    cb.like(cb.lower(rRoot.get("razon")), likePattern),
                    cb.like(cb.lower(rRoot.get("codigo")), likePattern)
            ));
        }

        if (estado != null && !estado.isEmpty()) {
            predicates.add(cb.equal(rRoot.get("estado"), estado));
        }

        if (clientePoliza != null) {
            predicates.add(cb.equal(rRoot.get("clientePoliza"), clientePoliza));
        }

        return predicates;
    }
}
