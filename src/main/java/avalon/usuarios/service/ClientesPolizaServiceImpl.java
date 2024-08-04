package avalon.usuarios.service;

import avalon.usuarios.data.ClientePolizaRepository;
import avalon.usuarios.data.ClienteRepository;
import avalon.usuarios.data.PolizaRepository;
import avalon.usuarios.data.UsuarioRepository;
import avalon.usuarios.model.pojo.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.aspectj.weaver.loadtime.Agent;
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
public class ClientesPolizaServiceImpl implements ClientesPolizaService {

    private final String ROL_ADMIN = "ADM";
    private final String ROL_ASESOR = "ASR";
    private final String ROL_AGENTE = "BRO";
    private final ClientePolizaRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public ClientesPolizaServiceImpl(ClientePolizaRepository repository) {
        this.repository = repository;
    }

    @Override
    public String generarNuevoCodigo() {
        String ultimoCodigo = (String) entityManager.createQuery("SELECT cp.codigo FROM ClientePoliza cp ORDER BY cp.codigo DESC")
                .setMaxResults(1)
                .getSingleResult();

        if (ultimoCodigo == null) {
            return "0000001";
        }

        int nuevoCodigoInt = Integer.parseInt(ultimoCodigo) + 1;
        return String.format("%07d", nuevoCodigoInt);
    }


    @Override
    public Page<ClientePoliza> searchClienesPolizas(String busqueda, Pageable pageable, Cliente cliente, Poliza poliza, Usuario usuario) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Consulta principal para los resultados paginados
        CriteriaQuery<ClientePoliza> query = cb.createQuery(ClientePoliza.class);
        Root<ClientePoliza> cmRoot = query.from(ClientePoliza.class);
        Join<ClientePoliza, Agente> agJoin = cmRoot.join("agente");
        Join<ClientePoliza, Cliente> cJoin = cmRoot.join("cliente");
        Join<ClientePoliza, Asesor> aJoin = cmRoot.join("asesor");
        Join<ClientePoliza, Poliza> pJoin = cmRoot.join("poliza");

        List<Predicate> predicates = buildPredicates(cb, cmRoot, agJoin, cJoin, aJoin, pJoin, busqueda, cliente, poliza, usuario);

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        Sort.Order sortOrder = pageable.getSort().iterator().next();
        Order order = sortOrder.isAscending() ? cb.asc(cmRoot.get(sortOrder.getProperty())) : cb.desc(cmRoot.get(sortOrder.getProperty()));
        query.orderBy(order);

        List<ClientePoliza> resultList = entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long totalRecords = countClientesMembresias(busqueda, cliente, poliza, usuario);

        return new PageImpl<>(resultList, pageable, totalRecords);
    }

    private Long countClientesMembresias(String busqueda, Cliente cliente, Poliza poliza, Usuario usuario) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Subconsulta para el conteo total de registros
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<ClientePoliza> countRoot = countQuery.from(ClientePoliza.class);
        countQuery.select(cb.count(countRoot));

        Join<ClientePoliza, Agente> agJoin = countRoot.join("agente");
        Join<ClientePoliza, Cliente> cJoin = countRoot.join("cliente");
        Join<ClientePoliza, Asesor> aJoin = countRoot.join("asesor");
        Join<ClientePoliza, Poliza> pJoin = countRoot.join("poliza");

        List<Predicate> countPredicates = buildPredicates(cb, countRoot, agJoin, cJoin, aJoin, pJoin, busqueda, cliente, poliza, usuario);

        countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));

        try {
            return entityManager.createQuery(countQuery).getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error en la consulta de conteo", e);
        }
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb,
                                            Root<ClientePoliza> cmRoot,
                                            Join<ClientePoliza, Agente> agJoin,
                                            Join<ClientePoliza, Cliente> cJoin,
                                            Join<ClientePoliza, Asesor> aJoin,
                                            Join<ClientePoliza, Poliza> pJoin,
                                            String busqueda,
                                            Cliente cliente,
                                            Poliza poliza,
                                            Usuario usuario) {
        List<Predicate> predicates = new ArrayList<>();

        if (busqueda != null && !busqueda.isEmpty()) {
            String likePattern = "%" + busqueda.toLowerCase() + "%";

            predicates.add(cb.or(
                    cb.like(cb.lower(cJoin.get("nombreUsuario")), likePattern),
                    cb.like(cb.lower(aJoin.get("nombreUsuario")), likePattern),
                    cb.like(cb.lower(agJoin.get("nombreUsuario")), likePattern),
                    cb.like(cb.lower(pJoin.get("nombre")), likePattern),
                    cb.like(cb.lower(cmRoot.get("codigo")), likePattern),
                    cb.like(cb.function("TO_CHAR", String.class, cmRoot.get("fechaInicio"), cb.literal("yyyy-MM-dd")), likePattern),
                    cb.like(cb.function("TO_CHAR", String.class, cmRoot.get("fechaFin"), cb.literal("yyyy-MM-dd")), likePattern)
            ));
        }

        if (cliente != null) {
            predicates.add(cb.equal(cmRoot.get("cliente"), cliente));
        }

        if (poliza != null) {
            predicates.add(cb.equal(cmRoot.get("poliza"), poliza));
        }

        if (usuario.getRol().getCodigo().equals(this.ROL_ASESOR)) {
            predicates.add(cb.equal(cmRoot.get("asesor").get("id"), usuario.getId()));
        } else if (usuario.getRol().getCodigo().equals(this.ROL_AGENTE)) {
            predicates.add(cb.equal(cmRoot.get("agente").get("id"), usuario.getId()));
        }

        return predicates;
    }

    @Override
    public Optional<ClientePoliza> getClientePoliza(Long clientePolizaId) {
        return this.repository.findById(clientePolizaId);
    }

    @Override
    public ClientePoliza savePoliza(ClientePoliza clientePoliza) {
        if (clientePoliza.getCodigo() == null) {
            clientePoliza.setCodigo(this.generarNuevoCodigo());
        }

        return this.repository.save(clientePoliza);
    }

    @Override
    public void deleteClientePoliza(Long clientePolizaId) {
        ClientePoliza clientePoliza = this.getClientePoliza(clientePolizaId).orElseThrow(() -> new IllegalArgumentException("Cliente Poliza no encontrada"));;
        this.repository.delete(clientePoliza);
    }
}
