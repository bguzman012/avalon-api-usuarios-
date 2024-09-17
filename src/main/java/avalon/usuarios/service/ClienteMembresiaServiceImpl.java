package avalon.usuarios.service;

import avalon.usuarios.data.*;
import avalon.usuarios.model.pojo.*;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteMembresiaServiceImpl implements ClienteMembresiaService {

    private final ClienteMembresiaRepository repository;
    @Autowired
    private ClienteService clienteService;
    @Autowired
    private MembresiaService membresiaService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public ClienteMembresiaServiceImpl(ClienteMembresiaRepository repository) {
        this.repository = repository;
    }

    @Override
    public ClienteMembresia saveClienteMembresia(ClienteMembresia clienteMembresia) {
        return repository.save(clienteMembresia);
    }

    public Page<ClienteMembresia> searchClientesMembresias(String busqueda, String estado, Pageable pageable, Cliente cliente, Membresia membresia) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Consulta principal para los resultados paginados
        CriteriaQuery<ClienteMembresia> query = cb.createQuery(ClienteMembresia.class);
        Root<ClienteMembresia> cmRoot = query.from(ClienteMembresia.class);

        List<Predicate> predicates = buildPredicates(cb, cmRoot, busqueda, estado, cliente, membresia);

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        Sort.Order sortOrder = pageable.getSort().iterator().next();
        Order order = sortOrder.isAscending() ? cb.asc(cmRoot.get(sortOrder.getProperty())) : cb.desc(cmRoot.get(sortOrder.getProperty()));
        query.orderBy(order);

        List<ClienteMembresia> resultList = entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long totalRecords = countClientesMembresias(busqueda, estado, cliente, membresia);

        return new PageImpl<>(resultList, pageable, totalRecords);
    }

    private Long countClientesMembresias(String busqueda, String estado, Cliente cliente, Membresia membresia) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Subconsulta para el conteo total de registros
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<ClienteMembresia> countRoot = countQuery.from(ClienteMembresia.class);
        countQuery.select(cb.count(countRoot));

        List<Predicate> countPredicates = buildPredicates(cb, countRoot, busqueda, estado, cliente, membresia);

        countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));

        try {
            return entityManager.createQuery(countQuery).getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error en la consulta de conteo", e);
        }
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<ClienteMembresia> cmRoot, String busqueda, String estado, Cliente cliente, Membresia membresia) {
        List<Predicate> predicates = new ArrayList<>();

        if (busqueda != null && !busqueda.isEmpty() && !busqueda.toLowerCase().equals("vencida") && !busqueda.toLowerCase().equals("activa")) {
            String likePattern = "%" + busqueda.toLowerCase() + "%";

            predicates.add(cb.or(
                    cb.like(cb.lower(cmRoot.get("cliente").get("nombreUsuario")), likePattern),
                    cb.like(cb.lower(cmRoot.get("membresia").get("nombres")), likePattern),
                    cb.like(cb.lower(cmRoot.get("asesor").get("nombreUsuario")), likePattern),
                    cb.like(cb.lower(cmRoot.get("codigo")), likePattern),
                    cb.like(cb.function("TO_CHAR", String.class, cmRoot.get("fechaInicio"), cb.literal("yyyy-MM-dd")), likePattern),
                    cb.like(cb.function("TO_CHAR", String.class, cmRoot.get("fechaFin"), cb.literal("yyyy-MM-dd")), likePattern)
            ));
        }

        if (estado != null && !estado.isEmpty() ){
            predicates.add(cb.equal(cmRoot.get("estado"), estado));
        }

        if (busqueda != null && !busqueda.isEmpty() && busqueda.toLowerCase().equals("vencida"))
            predicates.add(cb.equal(cmRoot.get("estado"), "V"));

        if (busqueda != null && !busqueda.isEmpty() && busqueda.toLowerCase().equals("activa"))
            predicates.add(cb.equal(cmRoot.get("estado"), "A"));



        if (cliente != null) {
            predicates.add(cb.equal(cmRoot.get("cliente"), cliente));
        }

        if (membresia != null) {
            predicates.add(cb.equal(cmRoot.get("membresia"), membresia));
        }

        return predicates;
    }

    @Override
    public Optional<ClienteMembresia> getClienteMembresia(Long id) {
        return this.repository.findById(id);
    }

    @Override
    public void deleteClienteMembresia(Long id) {
        repository.deleteById(id);
    }

    @Override
    public ByteArrayOutputStream generateExcelClientesPolizas(String busqueda, String sortField, String sortOrder, Cliente cliente, Membresia membresia) throws IOException {
        List<ClienteMembresia> allClientesMembresias = this.searchAllClientesMembresias(busqueda, sortField, sortOrder, cliente, membresia);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("clientes-membresias");

        Row headerRow = sheet.createRow(0);
        Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue("#");

        headerCell = headerRow.createCell(1);
        headerCell.setCellValue("CÓDIGO");

        headerCell = headerRow.createCell(2);
        headerCell.setCellValue("CLIENTE");

        headerCell = headerRow.createCell(3);
        headerCell.setCellValue("MEMBRESÍA");

        headerCell = headerRow.createCell(4);
        headerCell.setCellValue("ASESOR");

        headerCell = headerRow.createCell(5);
        headerCell.setCellValue("FECHA INICIO");

        headerCell = headerRow.createCell(6);
        headerCell.setCellValue("FECHA FIN");

        headerCell = headerRow.createCell(7);
        headerCell.setCellValue("ESTADO");

        int rowNum = 1;
        int registro = 1;
        for (ClienteMembresia clienteMembresia : allClientesMembresias) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue((int) registro);
            row.createCell(1).setCellValue((String) clienteMembresia.getCodigo());
            row.createCell(2).setCellValue((String) clienteMembresia.getCliente().getNombreUsuario());
            row.createCell(3).setCellValue((String) clienteMembresia.getMembresia().getNombres());
            row.createCell(4).setCellValue((String) clienteMembresia.getAsesor().getNombreUsuario());

            // Formatear fechas usando SimpleDateFormat
            Date fechaInicio = clienteMembresia.getFechaInicio();
            String fechaInicioStr = fechaInicio != null ? dateFormat.format(fechaInicio) : "";
            row.createCell(5).setCellValue(fechaInicioStr);

            Date fechaFin = clienteMembresia.getFechaFin();
            String fechaFinStr = fechaFin != null ? dateFormat.format(fechaFin) : "";
            row.createCell(6).setCellValue(fechaFinStr);

            String estado = (clienteMembresia.getEstado().equals("A")) ? "ACTIVA" : "VENCIDA";

            row.createCell(7).setCellValue((String) estado);

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
    public List<ClienteMembresia> searchAllClientesMembresias(String busqueda, String sortField, String sortOrder, Cliente cliente, Membresia membresia) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Consulta principal para los resultados paginados
        CriteriaQuery<ClienteMembresia> query = cb.createQuery(ClienteMembresia.class);
        Root<ClienteMembresia> cmRoot = query.from(ClienteMembresia.class);

        List<Predicate> predicates = buildPredicates(cb, cmRoot, busqueda, null, cliente, membresia);

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        Order order;
        if ("asc".equalsIgnoreCase(sortOrder)) {
            order = cb.asc(cmRoot.get(sortField));
        } else {
            order = cb.desc(cmRoot.get(sortField));
        }
        query.orderBy(order);

        List<ClienteMembresia> resultList = entityManager.createQuery(query)
                .getResultList();

        return resultList;
    }

    @Override
    public List<ClienteMembresia> findByEstadoAndCliente(String estado, Cliente cliente) {
        return repository.findAllByEstadoAndCliente(estado, cliente);
    }

    // Ejecutar cada 10 minutos
    @Scheduled(cron = "0 20 9 * * ?")
    @Transactional
    public void actualizarMembresiasVencidas() {
        cambiarEstadoMembresiasVencidas();
    }

    // Ejecutar al iniciar la aplicación
    @PostConstruct
    public void actualizarMembresiasAlIniciar() {
        cambiarEstadoMembresiasVencidas();
    }

    private void cambiarEstadoMembresiasVencidas() {
        List<ClienteMembresia> membresias = repository.findAllByEstado("A");

        Date hoy = new Date();
        for (ClienteMembresia membresia : membresias) {
            if (membresia.getFechaFin() != null && membresia.getFechaFin().before(hoy)) {
                membresia.setEstado("V");
                repository.save(membresia);
            }
        }
    }
}
