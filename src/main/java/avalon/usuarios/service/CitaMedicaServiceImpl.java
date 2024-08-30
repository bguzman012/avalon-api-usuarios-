package avalon.usuarios.service;

import avalon.usuarios.data.CitaMedicaRepository;
import avalon.usuarios.model.pojo.*;
import avalon.usuarios.model.request.PartiallyUpdateCitaMedicaRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
public class CitaMedicaServiceImpl implements CitaMedicaService {

    private final CitaMedicaRepository repository;
    private final UsuariosService usuariosService;

    private final String ROL_CLIENTE = "CLI";
    private final String ROL_ADMIN = "ADM";
    private final String ROL_ASESOR = "ASR";
    private final String ROL_AGENTE = "BRO";

    private final String ESTADO_POR_GESTIONAR = "N";
    private final String ESTADO_GESTIONADO = "G";
    private final String ESTADO_CERRADO = "C";

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public CitaMedicaServiceImpl(CitaMedicaRepository repository, @Qualifier("usuariosServiceImpl") UsuariosService usuariosService) {
        this.repository = repository;
        this.usuariosService = usuariosService;
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
        if (citaMedica == null || request.getEstado() == null) return null;

        citaMedica.setEstado(request.getEstado());
        return repository.save(citaMedica);
    }

    @Override
    public void deleteCitaMedica(Long citaMedicaId) {
        repository.deleteById(citaMedicaId);
    }

    @Override
    public List<CitaMedica> searchAllCitasMedicas(String busqueda, String sortField, String sortOrder, Caso caso) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Consulta principal para los resultados paginados
        CriteriaQuery<CitaMedica> query = cb.createQuery(CitaMedica.class);
        Root<CitaMedica> rRoot = query.from(CitaMedica.class);

        List<Predicate> predicates = buildAllPredicates(cb, rRoot, busqueda, caso);
        query.where(cb.and(predicates.toArray(new Predicate[0])));

        Order order;
        if ("asc".equalsIgnoreCase(sortOrder)) {
            order = cb.asc(rRoot.get(sortField));
        } else {
            order = cb.desc(rRoot.get(sortField));
        }
        query.orderBy(order);

        List<CitaMedica> resultList = entityManager.createQuery(query)
                .getResultList();

        return resultList;
    }

    @Override
    public ByteArrayOutputStream generateExcelCitasMedicas(String busqueda, String sortField, String sortOrder, Caso caso) throws IOException {
        List<CitaMedica> allCitasMedicas = this.searchAllCitasMedicas(busqueda, sortField, sortOrder, caso);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("citas-medicas");

        Row headerRow = sheet.createRow(0);
        Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue("#");

        headerCell = headerRow.createCell(1);
        headerCell.setCellValue("CASO");

        headerCell = headerRow.createCell(2);
        headerCell.setCellValue("CÓDIGO");

        headerCell = headerRow.createCell(3);
        headerCell.setCellValue("CLIENTE");

        headerCell = headerRow.createCell(4);
        headerCell.setCellValue("POLIZA");

        headerCell = headerRow.createCell(5);
        headerCell.setCellValue("CENTRO MÉDICO");

        headerCell = headerRow.createCell(6);
        headerCell.setCellValue("MÉDICO");

        headerCell = headerRow.createCell(7);
        headerCell.setCellValue("ASEGURADORA");

        headerCell = headerRow.createCell(8);
        headerCell.setCellValue("ESTADO");

        int rowNum = 1;
        int registro = 1;
        for(CitaMedica citaMedica: allCitasMedicas){
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue((int) registro);
            row.createCell(1).setCellValue((String) citaMedica.getCaso().getCodigo());
            row.createCell(2).setCellValue((String) citaMedica.getCodigo());
            row.createCell(3).setCellValue((String) citaMedica.getClientePoliza().getCliente().getNombreUsuario());
            row.createCell(4).setCellValue((String) citaMedica.getClientePoliza().getDisplayName());
            row.createCell(5).setCellValue((String) citaMedica.getMedicoCentroMedicoAseguradora().getCentroMedico().getNombre());
            row.createCell(6).setCellValue((String) citaMedica.getMedicoCentroMedicoAseguradora().getMedico().getNombres() +
                    " " + citaMedica.getMedicoCentroMedicoAseguradora().getMedico().getApellidos());
            row.createCell(7).setCellValue((String) citaMedica.getMedicoCentroMedicoAseguradora().getAseguradora().getNombre());

            String estado = "";
            if (citaMedica.getEstado().equals(this.ESTADO_POR_GESTIONAR))
                 estado = "POR GESTIONAR";

            if (citaMedica.getEstado().equals(this.ESTADO_GESTIONADO))
                estado = "GESTIONADO";

            if (citaMedica.getEstado().equals(this.ESTADO_CERRADO))
                estado = "CERRADO";

            row.createCell(8).setCellValue((String) estado);
            registro ++;
        }

        // Autoajustar el ancho de las columnas
        for (int i = 0; i < 3; i++) {
            sheet.autoSizeColumn(i);
        }

        // Escribir el libro de trabajo a un ByteArrayOutputStream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream;
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

    public Page<CitaMedica> searchCitasMedicas(String busqueda, String estado, Pageable pageable, ClientePoliza clientePoliza, Caso caso, Usuario usuario) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Consulta principal para los resultados paginados
        CriteriaQuery<CitaMedica> query = cb.createQuery(CitaMedica.class);
        Root<CitaMedica> rRoot = query.from(CitaMedica.class);

        List<Predicate> predicates = buildPredicates(cb, rRoot, busqueda, estado, clientePoliza, caso, usuario);

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        // Aplicar ordenación del pageable
        Sort.Order sortOrder = pageable.getSort().iterator().next();
        Order order = sortOrder.isAscending() ? cb.asc(rRoot.get(sortOrder.getProperty())) : cb.desc(rRoot.get(sortOrder.getProperty()));
        query.orderBy(order);

        List<CitaMedica> resultList = entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long totalRecords = countCitaMedicaes(busqueda, estado, clientePoliza, caso, usuario);

        return new PageImpl<>(resultList, pageable, totalRecords);
    }

    private Long countCitaMedicaes(String busqueda, String estado, ClientePoliza clientePoliza, Caso caso, Usuario usuario) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Subconsulta para el conteo total de registros
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<CitaMedica> countRoot = countQuery.from(CitaMedica.class);
        countQuery.select(cb.count(countRoot));

        List<Predicate> countPredicates = buildPredicates(cb, countRoot, busqueda, estado, clientePoliza, caso, usuario);

        countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));

        try {
            return entityManager.createQuery(countQuery).getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error en la consulta de conteo", e);
        }
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<CitaMedica> rRoot, String busqueda, String estado, ClientePoliza clientePoliza, Caso caso, Usuario usuario) {
        List<Predicate> predicates = new ArrayList<>();

        if (busqueda != null && !busqueda.isEmpty()) {
            String likePattern = "%" + busqueda.toLowerCase() + "%";

            predicates.add(cb.or(
                    cb.like(cb.lower(rRoot.get("codigo")), likePattern),
                    cb.like(cb.lower(rRoot.get("caso").get("codigo")), likePattern),
                    cb.like(cb.lower(rRoot.get("clientePoliza").get("cliente").get("nombreUsuario")), likePattern),
                    cb.like(cb.lower(rRoot.get("clientePoliza").get("poliza").get("nombre")), likePattern),

                    cb.like(cb.lower(rRoot.get("medicoCentroMedicoAseguradora").get("medico").get("nombres")), likePattern),
                    cb.like(cb.lower(rRoot.get("medicoCentroMedicoAseguradora").get("medico").get("apellidos")), likePattern),

                    cb.like(cb.lower(rRoot.get("medicoCentroMedicoAseguradora").get("centroMedico").get("nombre")), likePattern),
                    cb.like(cb.lower(rRoot.get("medicoCentroMedicoAseguradora").get("aseguradora").get("nombre")), likePattern)
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
        if (usuario!= null && usuario.getRol().getCodigo().equals(this.ROL_CLIENTE)) {
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

    private List<Predicate> buildAllPredicates(CriteriaBuilder cb, Root<CitaMedica> rRoot, String busqueda, Caso caso) {
        List<Predicate> predicates = new ArrayList<>();

        if (busqueda != null && !busqueda.isEmpty()) {
            String likePattern = "%" + busqueda.toLowerCase() + "%";

            predicates.add(cb.or(
                    cb.like(cb.lower(rRoot.get("codigo")), likePattern),
                    cb.like(cb.lower(rRoot.get("caso").get("codigo")), likePattern),
                    cb.like(cb.lower(rRoot.get("clientePoliza").get("cliente").get("nombreUsuario")), likePattern),
                    cb.like(cb.lower(rRoot.get("clientePoliza").get("poliza").get("nombre")), likePattern),

                    cb.like(cb.lower(rRoot.get("medicoCentroMedicoAseguradora").get("medico").get("nombres")), likePattern),
                    cb.like(cb.lower(rRoot.get("medicoCentroMedicoAseguradora").get("medico").get("apellidos")), likePattern),

                    cb.like(cb.lower(rRoot.get("medicoCentroMedicoAseguradora").get("centroMedico").get("nombre")), likePattern),
                    cb.like(cb.lower(rRoot.get("medicoCentroMedicoAseguradora").get("aseguradora").get("nombre")), likePattern)
            ));
        }
//
//        if (estado != null && !estado.isEmpty()) {
//            predicates.add(cb.equal(rRoot.get("estado"), estado));
//        }
        predicates.add(cb.notEqual(rRoot.get("estado"), "I"));

        if (caso != null) {
            predicates.add(cb.equal(rRoot.get("caso"), caso));
        }

        return predicates;
    }


}
