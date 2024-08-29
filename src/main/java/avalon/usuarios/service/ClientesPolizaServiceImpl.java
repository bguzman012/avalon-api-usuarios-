package avalon.usuarios.service;

import avalon.usuarios.data.ClientePolizaRepository;
import avalon.usuarios.data.ClienteRepository;
import avalon.usuarios.data.PolizaRepository;
import avalon.usuarios.data.UsuarioRepository;
import avalon.usuarios.model.pojo.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.aspectj.weaver.loadtime.Agent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ClientesPolizaServiceImpl implements ClientesPolizaService {

    private final String ROL_CLIENTE = "CLI";
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

        List<Predicate> predicates = buildPredicates(cb, cmRoot,busqueda, cliente, poliza, usuario);

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

    @Override
    public ByteArrayOutputStream generateExcelClientesPolizas() throws IOException {
        // Crear un libro de trabajo (Workbook) y una hoja (Sheet)
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Datos");

        // Crear una fila (Row) y una celda (Cell)
        Row headerRow = sheet.createRow(0);
        Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue("ID");

        headerCell = headerRow.createCell(1);
        headerCell.setCellValue("Nombre");

        headerCell = headerRow.createCell(2);
        headerCell.setCellValue("Apellido");

        // Agregar datos a las filas
        List<Object[]> data = getData(); // Método para obtener los datos
        int rowNum = 1;
        for (Object[] datum : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue((Integer) datum[0]);
            row.createCell(1).setCellValue((String) datum[1]);
            row.createCell(2).setCellValue((String) datum[2]);
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

    // Método ficticio para obtener los datos
    private List<Object[]> getData() {
        return List.of(
                new Object[]{1, "John", "Doe"},
                new Object[]{2, "Jane", "Doe"},
                new Object[]{3, "Michael", "Smith"}
        );
    }

    private Long countClientesMembresias(String busqueda, Cliente cliente, Poliza poliza, Usuario usuario) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Subconsulta para el conteo total de registros
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<ClientePoliza> countRoot = countQuery.from(ClientePoliza.class);
        countQuery.select(cb.count(countRoot));

        List<Predicate> countPredicates = buildPredicates(cb, countRoot, busqueda, cliente, poliza, usuario);

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
                                            String busqueda,
                                            Cliente cliente,
                                            Poliza poliza,
                                            Usuario usuario) {
        List<Predicate> predicates = new ArrayList<>();

        if (busqueda != null && !busqueda.isEmpty()) {
            String likePattern = "%" + busqueda.toLowerCase() + "%";

            predicates.add(cb.or(
                    cb.like(cb.lower(cmRoot.get("cliente").get("nombreUsuario")), likePattern),
                    cb.like(cb.lower(cmRoot.get("asesor").get("nombreUsuario")), likePattern),
                    cb.like(cb.lower(cmRoot.get("agente").get("nombreUsuario")), likePattern),
                    cb.like(cb.lower(cmRoot.get("poliza").get("nombre")), likePattern),
                    cb.like(cb.lower(cmRoot.get("codigo")), likePattern),
                    cb.like(cb.lower(cmRoot.get("numeroCertificado")), likePattern),
                    cb.like(cb.function("TO_CHAR", String.class, cmRoot.get("fechaInicio"), cb.literal("yyyy-MM-dd")), likePattern),
                    cb.like(cb.function("TO_CHAR", String.class, cmRoot.get("fechaFin"), cb.literal("yyyy-MM-dd")), likePattern)
            ));
        }

        // Si se esta consultando las polizas por cliente y si el usuario loggeado es cliente
        if (usuario.getRol().getCodigo().equals(this.ROL_CLIENTE) && cliente != null){
            // Si el cliente loggeado es diferente al cliente que se esta consultando
            if (!Objects.equals(usuario.getId(), cliente.getId())){
                predicates.add(cb.equal(cmRoot.get("titular").get("cliente").get("id"), usuario.getId()));
            }
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
