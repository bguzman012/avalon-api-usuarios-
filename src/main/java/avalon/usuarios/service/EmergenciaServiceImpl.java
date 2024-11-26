package avalon.usuarios.service;

import avalon.usuarios.data.EmergenciaRepository;
import avalon.usuarios.model.pojo.*;
import avalon.usuarios.model.request.PartiallyUpdateEmergenciasRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
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
import java.util.Optional;

@Service
public class EmergenciaServiceImpl implements EmergenciaService {

    private final EmergenciaRepository repository;
    private final UsuariosService usuariosService;

    private final String ROL_CLIENTE = "CLI";
    private final String ROL_ADMIN = "ADM";
    private final String ROL_ASESOR = "ASR";
    private final String ROL_AGENTE = "BRO";
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public EmergenciaServiceImpl(EmergenciaRepository repository, @Qualifier("usuariosServiceImpl") UsuariosService usuariosService) {
        this.repository = repository;
        this.usuariosService = usuariosService;
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
        if (emergencia == null || request.getEstado() == null) return null;

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

    public Page<Emergencia> searchEmergencias(String busqueda, String estado, Pageable pageable, ClientePoliza clientePoliza, Caso caso, Usuario usuario) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Consulta principal para los resultados paginados
        CriteriaQuery<Emergencia> query = cb.createQuery(Emergencia.class);
        Root<Emergencia> rRoot = query.from(Emergencia.class);

        List<Predicate> predicates = buildPredicates(cb, rRoot, busqueda, estado, clientePoliza, caso, usuario);

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        // Aplicar ordenación del pageable
        Sort.Order sortOrder = pageable.getSort().iterator().next();
        Order order = sortOrder.isAscending() ? cb.asc(rRoot.get(sortOrder.getProperty())) : cb.desc(rRoot.get(sortOrder.getProperty()));
        query.orderBy(order);

        List<Emergencia> resultList = entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long totalRecords = countEmergencias(busqueda, estado, clientePoliza, caso, usuario);

        return new PageImpl<>(resultList, pageable, totalRecords);
    }

    private Long countEmergencias(String busqueda, String estado, ClientePoliza clientePoliza, Caso caso, Usuario usuario) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Subconsulta para el conteo total de registros
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Emergencia> countRoot = countQuery.from(Emergencia.class);
        countQuery.select(cb.count(countRoot));

        List<Predicate> countPredicates = buildPredicates(cb, countRoot,busqueda, estado, clientePoliza, caso, usuario);

        countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));

        try {
            return entityManager.createQuery(countQuery).getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error en la consulta de conteo", e);
        }
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<Emergencia> rRoot, String busqueda, String estado, ClientePoliza clientePoliza, Caso caso, Usuario usuario) {
        List<Predicate> predicates = new ArrayList<>();
        if (busqueda != null && !busqueda.isEmpty()) {
            String likePattern = "%" + busqueda.toLowerCase() + "%";

            // Realiza LEFT JOINs para las relaciones necesarias
            Join<Emergencia, Caso> casoJoin = rRoot.join("caso", JoinType.LEFT);
            Join<Emergencia, ClientePoliza> clientePolizaJoin = rRoot.join("clientePoliza", JoinType.LEFT);
            Join<ClientePoliza, Cliente> clienteJoin = clientePolizaJoin.join("cliente", JoinType.LEFT);
            Join<ClientePoliza, Poliza> polizaJoin = clientePolizaJoin.join("poliza", JoinType.LEFT);
            Join<CitaMedica, MedicoCentroMedicoAseguradora> medicoCentroJoin = rRoot.join("medicoCentroMedicoAseguradora", JoinType.LEFT);
            Join<MedicoCentroMedicoAseguradora, Medico> medicoJoin = medicoCentroJoin.join("medico", JoinType.LEFT);
            Join<MedicoCentroMedicoAseguradora, CentroMedico> centroMedicoJoin = medicoCentroJoin.join("centroMedico", JoinType.LEFT);
            Join<MedicoCentroMedicoAseguradora, Aseguradora> aseguradoraJoin = medicoCentroJoin.join("aseguradora", JoinType.LEFT);

            // Agrega los predicados con las relaciones de LEFT JOIN
            predicates.add(cb.or(
                    cb.like(cb.lower(rRoot.get("codigo")), likePattern),
                    cb.like(cb.lower(casoJoin.get("codigo")), likePattern),
                    cb.like(cb.lower(clienteJoin.get("nombreUsuario")), likePattern),
                    cb.like(cb.lower(clienteJoin.get("nombres")), likePattern),
                    cb.like(cb.lower(clienteJoin.get("nombresDos")), likePattern),
                    cb.like(cb.lower(clienteJoin.get("apellidos")), likePattern),
                    cb.like(cb.lower(clienteJoin.get("apellidosDos")), likePattern),
                    cb.like(cb.lower(polizaJoin.get("nombre")), likePattern),
                    cb.like(cb.lower(medicoJoin.get("nombres")), likePattern),
                    cb.like(cb.lower(medicoJoin.get("apellidos")), likePattern),
                    cb.like(cb.lower(centroMedicoJoin.get("nombre")), likePattern),
                    cb.like(cb.lower(aseguradoraJoin.get("nombre")), likePattern)
            ));
        }

        if (estado != null && !estado.isEmpty()) {
            predicates.add(cb.equal(rRoot.get("estado"), estado));
        }

        if (usuario != null && (estado == null || estado.isEmpty()) && !usuario.getRol().getCodigo().equals(this.ROL_ADMIN)) {
            predicates.add(cb.notEqual(rRoot.get("estado"), "I"));
        }

        if (clientePoliza != null) {
            predicates.add(cb.equal(rRoot.get("clientePoliza"), clientePoliza));
        }

        if (caso != null) {
            predicates.add(cb.equal(rRoot.get("caso"), caso));
        }

        if (usuario!= null && usuario.getRol().getCodigo().equals(this.ROL_ASESOR))
            predicates.add(cb.equal(rRoot.get("clientePoliza").get("asesor").get("id"), usuario.getId()));

        if (usuario!= null && usuario.getRol().getCodigo().equals(this.ROL_AGENTE))
            predicates.add(cb.equal(rRoot.get("clientePoliza").get("agente").get("id"), usuario.getId()));

        // Si el usuario loggeado es cliente, se obtiene todos los casos del cliente,
        // o se obtiene tambien los casos de los dependientes menores de 18 años donde sea titular el cliente loggeado
        if (usuario.getRol().getCodigo().equals(this.ROL_CLIENTE)) {
            LocalDate today = LocalDate.now();
            LocalDate eighteenYearsAgo = today.minusYears(18);
            Date fechaLimite = Date.from(eighteenYearsAgo.atStartOfDay(ZoneId.systemDefault()).toInstant());

            // Predicado para cliente directo
            Predicate clienteDirecto = cb.equal(rRoot.get("clientePoliza").get("cliente").get("id"), usuario.getId());

            // Predicado para dependientes menores de 18 años
            Join<ClientePoliza, ClientePoliza> titularJoin = rRoot.join("clientePoliza", JoinType.LEFT).join("titular", JoinType.LEFT);

            Predicate titularCliente = cb.equal(titularJoin.get("cliente").get("id"), usuario.getId());
            Predicate dependienteMenorDe18 = cb.greaterThan(rRoot.get("clientePoliza").get("cliente").get("fechaNacimiento"), fechaLimite);

            predicates.add(cb.or(clienteDirecto, cb.and(titularCliente, dependienteMenorDe18)));
        }

        return predicates;
    }
}
