package avalon.usuarios.service;

import avalon.usuarios.data.CitaMedicaRepository;
import avalon.usuarios.model.pojo.Caso;
import avalon.usuarios.model.pojo.ClientePoliza;
import avalon.usuarios.model.pojo.CitaMedica;
import avalon.usuarios.model.pojo.Reclamacion;
import avalon.usuarios.model.request.PartiallyUpdateCitaMedicaRequest;
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
public class CitaMedicaServiceImpl implements CitaMedicaService {

    private final CitaMedicaRepository repository;
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public CitaMedicaServiceImpl(CitaMedicaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<CitaMedica> getCitaMedica(Long citaMedicaId) {
        return repository.findById(citaMedicaId);
    }

    @Override
    public CitaMedica saveCitaMedica(CitaMedica citaMedica) {
        if (citaMedica.getCodigo() == null) {
            citaMedica.setCodigo(this.generarNuevoCodigo());
        }
        return repository.save(citaMedica);
    }

    @Override
    public CitaMedica partiallyUpdateCitaMedica(PartiallyUpdateCitaMedicaRequest request, Long citaMedicaId) {
        CitaMedica citaMedica = repository.findById(citaMedicaId).orElse(null);
        if (citaMedica == null) return null;

        if (request.getEstado() != null)
            citaMedica.setEstado(request.getEstado());

        return repository.save(citaMedica);
    }

    @Override
    public void deleteCitaMedica(Long citaMedicaId) {
        repository.deleteById(citaMedicaId);
    }

    @Override
    public String generarNuevoCodigo() {
        try {
            String ultimoCodigo = (String) entityManager.createQuery("SELECT c.codigo FROM CitaMedica c ORDER BY c.codigo DESC")
                    .setMaxResults(1)
                    .getSingleResult();

            int nuevoCodigoInt = Integer.parseInt(ultimoCodigo) + 1;
            return String.format("%07d", nuevoCodigoInt);
        } catch (NoResultException e) {
            // Si no hay resultados, se devuelve el primer código
            return "0000001";
        }
    }

    public Page<CitaMedica> searchCitasMedicas(String busqueda, String estado, Pageable pageable, ClientePoliza clientePoliza, Caso caso) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Consulta principal para los resultados paginados
        CriteriaQuery<CitaMedica> query = cb.createQuery(CitaMedica.class);
        Root<CitaMedica> rRoot = query.from(CitaMedica.class);

        List<Predicate> predicates = buildPredicates(cb, rRoot, busqueda, estado, clientePoliza, caso);

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        // Aplicar ordenación del pageable
        Sort.Order sortOrder = pageable.getSort().iterator().next();
        Order order = sortOrder.isAscending() ? cb.asc(rRoot.get(sortOrder.getProperty())) : cb.desc(rRoot.get(sortOrder.getProperty()));
        query.orderBy(order);

        List<CitaMedica> resultList = entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long totalRecords = countCitaMedicaes(busqueda, estado, clientePoliza, caso);

        return new PageImpl<>(resultList, pageable, totalRecords);
    }

    private Long countCitaMedicaes(String busqueda, String estado, ClientePoliza clientePoliza, Caso caso) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Subconsulta para el conteo total de registros
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<CitaMedica> countRoot = countQuery.from(CitaMedica.class);
        countQuery.select(cb.count(countRoot));

        List<Predicate> countPredicates = buildPredicates(cb, countRoot, busqueda, estado, clientePoliza, caso);

        countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));

        try {
            return entityManager.createQuery(countQuery).getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error en la consulta de conteo", e);
        }
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<CitaMedica> rRoot, String busqueda, String estado, ClientePoliza clientePoliza, Caso caso) {
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

        if (caso != null) {
            predicates.add(cb.equal(rRoot.get("caso"), caso));
        }

        return predicates;
    }
}
