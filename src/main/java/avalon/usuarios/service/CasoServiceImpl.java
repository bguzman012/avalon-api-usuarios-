package avalon.usuarios.service;

import avalon.usuarios.data.CasoRepository;
import avalon.usuarios.model.pojo.*;
import avalon.usuarios.model.response.PaginatedResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
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
public class CasoServiceImpl implements CasoService {

    private final CasoRepository repository;
    private final String ROL_CLIENTE = "CLI";
    private final String ROL_ASESOR = "ASR";
    private final String ROL_AGENTE = "BRO";

    private final String ESTADO_POR_GESTIONAR = "N";
    private final String ESTADO_GESTIONADO = "G";
    private final String ESTADO_CERRADO = "C";

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public CasoServiceImpl(CasoRepository repository) {
        this.repository = repository;
    }


    @Override
    public Page<Caso> searchCasos(String busqueda, Pageable pageable, ClientePoliza clientePoliza, Usuario usuario) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Consulta principal para los resultados paginados
        CriteriaQuery<Caso> query = cb.createQuery(Caso.class);
        Root<Caso> root = query.from(Caso.class);

        List<Predicate> predicates = buildPredicates(cb, root, busqueda, clientePoliza, usuario);

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        Sort.Order sortOrder = pageable.getSort().iterator().next();
        Order order = sortOrder.isAscending() ? cb.asc(root.get(sortOrder.getProperty())) : cb.desc(root.get(sortOrder.getProperty()));
        query.orderBy(order);

        List<Caso> resultList = entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long totalRecords = countCasos(busqueda, clientePoliza, usuario);
        return new PageImpl<>(resultList, pageable, totalRecords);
    }

    private Long countCasos(String busqueda, ClientePoliza clientePoliza, Usuario usuario) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Subconsulta para el conteo total de registros
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Caso> countRoot = countQuery.from(Caso.class);
        countQuery.select(cb.count(countRoot));

        List<Predicate> countPredicates = buildPredicates(cb, countRoot, busqueda, clientePoliza, usuario);

        countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));

        try {
            return entityManager.createQuery(countQuery).getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error en la consulta de conteo", e);
        }
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb,
                                            Root<Caso> root,
                                            String busqueda,
                                            ClientePoliza clientePoliza,
                                            Usuario usuario) {
        List<Predicate> predicates = new ArrayList<>();

        if (busqueda != null && !busqueda.isEmpty()) {
            String likePattern = "%" + busqueda.toLowerCase() + "%";

            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("codigo")), likePattern),
                    cb.like(cb.lower(root.get("observaciones")), likePattern),

                    cb.like(cb.lower(root.get("clientePoliza").get("cliente").get("nombreUsuario")), likePattern),
                    cb.like(cb.lower(root.get("clientePoliza").get("cliente").get("nombres")), likePattern),
                    cb.like(cb.lower(root.get("clientePoliza").get("cliente").get("apellidos")), likePattern),

                    cb.like(cb.lower(root.get("clientePoliza").get("poliza").get("nombre")), likePattern)
            ));
        }
//
        if (clientePoliza != null) {
            predicates.add(cb.equal(root.get("clientePoliza").get("id"), clientePoliza.getId()));
        }

        if (usuario != null && usuario.getRol().getCodigo().equals(this.ROL_ASESOR))
            predicates.add(cb.equal(root.get("clientePoliza").get("asesor").get("id"), usuario.getId()));

        if (usuario != null && usuario.getRol().getCodigo().equals(this.ROL_AGENTE))
            predicates.add(cb.equal(root.get("clientePoliza").get("agente").get("id"), usuario.getId()));

        // Si el usuario loggeado es cliente, se obtiene todos los casos del cliente,
        // o se obtiene tambien los casos de los dependientes menores de 18 años donde sea titular el cliente loggeado
        if (usuario != null && usuario.getRol().getCodigo().equals(this.ROL_CLIENTE)) {
            LocalDate today = LocalDate.now();
            LocalDate eighteenYearsAgo = today.minusYears(18);
            Date fechaLimite = Date.from(eighteenYearsAgo.atStartOfDay(ZoneId.systemDefault()).toInstant());

            // Predicado para cliente directo
            Predicate clienteDirecto = cb.equal(root.get("clientePoliza").get("cliente").get("id"), usuario.getId());

            // Predicado para dependientes menores de 18 años
            Join<ClientePoliza, ClientePoliza> titularJoin = root.join("clientePoliza", JoinType.LEFT).join("titular", JoinType.LEFT);

            Predicate titularCliente = cb.equal(titularJoin.get("cliente").get("id"), usuario.getId());
            Predicate dependienteMenorDe18 = cb.greaterThan(root.get("clientePoliza").get("cliente").get("fechaNacimiento"), fechaLimite);

            predicates.add(cb.or(clienteDirecto, cb.and(titularCliente, dependienteMenorDe18)));
        }

        return predicates;
    }

    @Override
    public Optional<Caso> getCaso(Long casoId) {
        return repository.findById(casoId);
    }

    @Override
    public Caso saveCaso(Caso caso) {
        if (caso.getCodigo() == null) {
            caso.setCodigo(this.generarNuevoCodigo());
        }
        return repository.save(caso);
    }

    @Override
    public void deleteCaso(Long casoId) {
        repository.deleteById(casoId);
    }

    @Override
    public List<Caso> searchAllCasos(String busqueda, String sortField, String sortOrder, ClientePoliza clientePoliza) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Consulta principal para los resultados paginados
        CriteriaQuery<Caso> query = cb.createQuery(Caso.class);
        Root<Caso> root = query.from(Caso.class);

        List<Predicate> predicates = buildPredicates(cb, root, busqueda, clientePoliza, null);

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        Order order;
        if ("asc".equalsIgnoreCase(sortOrder)) {
            order = cb.asc(root.get(sortField));
        } else {
            order = cb.desc(root.get(sortField));
        }
        query.orderBy(order);

        List<Caso> resultList = entityManager.createQuery(query)
                .getResultList();

        return resultList;
    }

    @Override
    public PaginatedResponse<Object> getCasosTrack(String busqueda, int pageNumber, int pageSize) {
        String order = " ORDER BY caso_id, created_date";

        String busqueda_like  = null;
        if (busqueda != null && !busqueda.isEmpty() && !busqueda.isBlank())
            busqueda_like = "LOWER('%" + busqueda + "%')";

        String filtros = " WHERE LOWER(caso_codigo) LIKE " + busqueda_like +
                "OR LOWER(nombre_usuario) LIKE " + busqueda_like +
                "OR LOWER(tipo) LIKE " + busqueda_like +
                "OR LOWER(medico) LIKE " + busqueda_like +
                "OR LOWER(aseguradora) LIKE " + busqueda_like +
                "OR LOWER(centro_medico) LIKE " + busqueda_like;

        String sqlSelect = "SELECT * " + getBaseSqlUnion();

        if (busqueda != null && !busqueda.isEmpty() && !busqueda.isBlank())
            sqlSelect += filtros;

        sqlSelect += order;

        Query query = entityManager.createNativeQuery(sqlSelect);
        query.setFirstResult(pageNumber * pageSize);  // Salta los registros de las páginas anteriores
        query.setMaxResults(pageSize);  // Establece el número máximo de resultados por página

        PaginatedResponse<Object> response = new PaginatedResponse<>(query.getResultList(),
                getTotalRecords(busqueda));
        return response;
    }

    private String getBaseSqlUnion() {
        return """
                FROM (
                                    select citas.caso_id, caso.codigo as caso_codigo, citas.codigo as codigo_actividad, concat(cp.codigo, ' - ', pol.nombre) as cli_pol_codigo, usu.nombre_usuario, citas.estado, citas.created_date,
                                           'CITA' as tipo, concat(med.nombres, ' ', med.apellidos) medico, aseg.nombre aseguradora, cm.nombre centro_medico
                                    from citas_medicas citas
                                             JOIN casos caso on citas.caso_id = caso.id
                                             JOIN clientes_polizas cp on citas.cliente_poliza_id = cp.id
                                             JOIN polizas pol on cp.poliza_id = pol.id
                                             JOIN usuarios usu on cp.cliente_id = usu.id
                                            LEFT JOIN medico_centr_med_aseg mcma on citas.medico_centro_medico_aseguradora_id = mcma.id
                                            JOIN medicos med on mcma.medico_id = med.id
                                            JOIN aseguradoras aseg on mcma.aseguradora_id = aseg.id
                                            JOIN centros_medicos cm on mcma.centro_medico_id = cm.id
                                    UNION
                                
                                    select eme.caso_id, caso.codigo as caso_codigo, eme.codigo as codigo_actividad, concat(cp.codigo, ' - ', pol.nombre) as cli_pol_codigo, usu.nombre_usuario, eme.estado, eme.created_date,
                                           'EMERGENCIA' as tipo, concat(med.nombres, ' ', med.apellidos) medico, aseg.nombre aseguradora, cm.nombre centro_medico
                                    from emergencias eme
                                             JOIN casos caso on eme.caso_id = caso.id
                                             JOIN clientes_polizas cp on eme.cliente_poliza_id = cp.id
                                             JOIN polizas pol on cp.poliza_id = pol.id
                                             JOIN usuarios usu on cp.cliente_id = usu.id
                                            LEFT JOIN medico_centr_med_aseg mcma on eme.medico_centro_medico_aseguradora_id = mcma.id
                                            JOIN medicos med on mcma.medico_id = med.id
                                            JOIN aseguradoras aseg on mcma.aseguradora_id = aseg.id
                                            JOIN centros_medicos cm on mcma.centro_medico_id = cm.id
                                    UNION
                                    select rec.caso_id, caso.codigo as caso_codigo, rec.codigo as codigo_actividad, concat(cp.codigo, ' - ', pol.nombre) as cli_pol_codigo, usu.nombre_usuario, rec.estado, rec.created_date,
                                           'RECLAMACION' as tipo, concat(med.nombres, ' ', med.apellidos) medico, aseg.nombre aseguradora, cm.nombre centro_medico
                                    from reclamaciones rec
                                             JOIN casos caso on rec.caso_id = caso.id
                                             JOIN clientes_polizas cp on rec.cliente_poliza_id = cp.id
                                             JOIN polizas pol on cp.poliza_id = pol.id
                                             JOIN usuarios usu on cp.cliente_id = usu.id
                                            LEFT JOIN medico_centr_med_aseg mcma on rec.medico_centro_medico_aseguradora_id = mcma.id
                                            JOIN medicos med on mcma.medico_id = med.id
                                            JOIN aseguradoras aseg on mcma.aseguradora_id = aseg.id
                                            JOIN centros_medicos cm on mcma.centro_medico_id = cm.id
                                ) AS QUERY_UNION""";
    }

    private Long getTotalRecords(String busqueda) {
        String sql = "SELECT COUNT(*) " + getBaseSqlUnion();

        String busqueda_like = null;
        if (busqueda != null && !busqueda.isEmpty() && !busqueda.isBlank())
            busqueda_like = "LOWER('%" + busqueda + "%')";

        String filtros = " WHERE LOWER(caso_codigo) LIKE " + busqueda_like +
                "OR LOWER(nombre_usuario) LIKE " + busqueda_like +
                "OR LOWER(tipo) LIKE " + busqueda_like +
                "OR LOWER(medico) LIKE " + busqueda_like +
                "OR LOWER(aseguradora) LIKE " + busqueda_like +
                "OR LOWER(centro_medico) LIKE " + busqueda_like;

        if (busqueda != null && !busqueda.isEmpty() && !busqueda.isBlank())
            sql += filtros;

        Query query = entityManager.createNativeQuery(sql);
        return (Long) query.getSingleResult();
    }

    @Override
    public ByteArrayOutputStream generateExcelCasosTrack(String busqueda) throws IOException {
        String order = " ORDER BY caso_id, created_date";

        String busqueda_like  = null;
        if (busqueda != null && !busqueda.isEmpty() && !busqueda.isBlank())
            busqueda_like = "LOWER('%" + busqueda + "%')";

        String filtros = " WHERE LOWER(caso_codigo) LIKE " + busqueda_like +
                "OR LOWER(nombre_usuario) LIKE " + busqueda_like +
                "OR LOWER(tipo) LIKE " + busqueda_like +
                "OR LOWER(medico) LIKE " + busqueda_like +
                "OR LOWER(aseguradora) LIKE " + busqueda_like +
                "OR LOWER(centro_medico) LIKE " + busqueda_like;

        String sqlSelect = "SELECT * " + getBaseSqlUnion();

        if (busqueda != null && !busqueda.isEmpty() && !busqueda.isBlank())
            sqlSelect += filtros;

        sqlSelect += order;

        Query query = entityManager.createNativeQuery(sqlSelect);

        List<Object[]> results = query.getResultList();


        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("atenciones-casos");

        Row headerRow = sheet.createRow(0);
        Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue("#");

        headerCell = headerRow.createCell(1);
        headerCell.setCellValue("CÓDIGO CASO ");

        headerCell = headerRow.createCell(2);
        headerCell.setCellValue("TIPO ACTIVIDAD");

        headerCell = headerRow.createCell(3);
        headerCell.setCellValue("CÓDIGO ACTIVIDAD");

        headerCell = headerRow.createCell(4);
        headerCell.setCellValue("CLIENTE");

        headerCell = headerRow.createCell(5);
        headerCell.setCellValue("PÓLIZA");

        headerCell = headerRow.createCell(6);
        headerCell.setCellValue("ASEGURADORA");

        headerCell = headerRow.createCell(7);
        headerCell.setCellValue("CENTRO MÉDICO");

        headerCell = headerRow.createCell(8);
        headerCell.setCellValue("MÉDICO");

        headerCell = headerRow.createCell(9);
        headerCell.setCellValue("ESTADO");

        int rowNum = 1;
        int registro = 1;

        for (int i = 0; i < results.size(); i++) {
            Object[] resultRow = results.get(i);
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue((int) registro);
            row.createCell(1).setCellValue((String) resultRow[1]);
            row.createCell(2).setCellValue((String) resultRow[7]);
            row.createCell(3).setCellValue((String) resultRow[2]);
            row.createCell(4).setCellValue((String) resultRow[4]);
            row.createCell(5).setCellValue((String) resultRow[3]);
            row.createCell(6).setCellValue((String) resultRow[9]);
            row.createCell(7).setCellValue((String) resultRow[10]);
            row.createCell(8).setCellValue((String) resultRow[8]);

            String estado = "";
            if (resultRow[5].equals(this.ESTADO_POR_GESTIONAR))
                estado = "POR GESTIONAR";

            if (resultRow[5].equals(this.ESTADO_GESTIONADO))
                estado = "GESTIONADO";

            if (resultRow[5].equals(this.ESTADO_CERRADO))
                estado = "CERRADO";

            row.createCell(9).setCellValue((String) estado);
            registro++;
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
    public ByteArrayOutputStream generateExcelCasos(String busqueda, String sortField, String sortOrder, ClientePoliza clientePoliza) throws IOException {
        List<Caso> allCasos = this.searchAllCasos(busqueda, sortField, sortOrder, clientePoliza);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("casos");

        Row headerRow = sheet.createRow(0);
        Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue("#");

        headerCell = headerRow.createCell(1);
        headerCell.setCellValue("CÓDIGO");

        headerCell = headerRow.createCell(2);
        headerCell.setCellValue("CLIENTE");

        headerCell = headerRow.createCell(3);
        headerCell.setCellValue("PÓLIZA");

        int rowNum = 1;
        int registro = 1;
        for (Caso caso : allCasos) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue((int) registro);
            row.createCell(1).setCellValue((String) caso.getCodigo());
            row.createCell(2).setCellValue((String) caso.getClienteDisplayName());
            row.createCell(3).setCellValue((String) caso.getClientePoliza().getDisplayName());

            registro++;
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

    public String generarNuevoCodigo() {
        try {
            String ultimoCodigo = (String) entityManager.createQuery("SELECT c.codigo FROM Caso c ORDER BY c.codigo DESC")
                    .setMaxResults(1)
                    .getSingleResult();

            int nuevoCodigoInt = Integer.parseInt(ultimoCodigo) + 1;
            return String.format("%07d", nuevoCodigoInt);
        } catch (NoResultException e) {
            // Si no hay resultados, se devuelve el primer código
            return "0000001";
        }
    }

}
