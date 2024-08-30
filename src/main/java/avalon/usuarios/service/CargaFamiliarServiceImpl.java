package avalon.usuarios.service;

import avalon.usuarios.data.CargaFamiliarRepository;
import avalon.usuarios.model.pojo.ClientePoliza;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public ByteArrayOutputStream generateExcelClientesPolizas(ClientePoliza clientePoliza, String busqueda, String sortField, String sortOrder) throws IOException {
        List<ClientePoliza> allClientesPolizas = this.searchAllCargasFamiliares(clientePoliza, busqueda, sortField, sortOrder);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("cargas-familiares");

        if (clientePoliza!= null){
            Row encabezadoClientePolizaRow = sheet.createRow(0);
            Cell headerCell = encabezadoClientePolizaRow.createCell(0);
            headerCell.setCellValue("TITULAR: ");

            headerCell = encabezadoClientePolizaRow.createCell(1);
            headerCell.setCellValue(clientePoliza.getDisplayName());

            headerCell = encabezadoClientePolizaRow.createCell(3);
            headerCell.setCellValue("USUARIO: ");

            headerCell = encabezadoClientePolizaRow.createCell(4);
            headerCell.setCellValue(clientePoliza.getCliente().getNombreUsuario());
        }

        Row headerRow = sheet.createRow(clientePoliza == null ? 0 : 2);
        Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue("#");

        headerCell = headerRow.createCell(1);
        headerCell.setCellValue("POLIZA");

        headerCell = headerRow.createCell(2);
        headerCell.setCellValue("NUM. CERTIFICADO");

        headerCell = headerRow.createCell(3);
        headerCell.setCellValue("NOMBRE");

        headerCell = headerRow.createCell(4);
        headerCell.setCellValue("CORREO ELECTRÓNICO");

        headerCell = headerRow.createCell(5);
        headerCell.setCellValue("NOMBRE USUARIO");

        headerCell = headerRow.createCell(6);
        headerCell.setCellValue("PARENTESCO");


        int rowNum = clientePoliza == null ? 1 : 3;
        int registro = 1;
        for(ClientePoliza clientePolizaObj: allClientesPolizas){
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue((int) registro);
            row.createCell(1).setCellValue((String) clientePolizaObj.getDisplayName());
            row.createCell(2).setCellValue((String) clientePolizaObj.getNumeroCertificado());
            row.createCell(3).setCellValue((String) clientePolizaObj.getCliente().getNombres() + " " + clientePolizaObj.getCliente().getApellidos());
            row.createCell(4).setCellValue((String) clientePolizaObj.getCliente().getCorreoElectronico());
            row.createCell(5).setCellValue((String) clientePolizaObj.getCliente().getNombreUsuario());
            row.createCell(6).setCellValue((String) clientePolizaObj.getParentesco());

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
    public List<ClientePoliza> searchAllCargasFamiliares(ClientePoliza clientePoliza, String busqueda, String sortField, String sortOrder) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Consulta principal para los resultados paginados
        CriteriaQuery<ClientePoliza> query = cb.createQuery(ClientePoliza.class);
        Root<ClientePoliza> root = query.from(ClientePoliza.class);

        List<Predicate> predicates = buildAllPredicates(cb, root, busqueda, clientePoliza);

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        Order order;
        if ("asc".equalsIgnoreCase(sortOrder)) {
            order = cb.asc(root.get(sortField));
        } else {
            order = cb.desc(root.get(sortField));
        }
        query.orderBy(order);

        List<ClientePoliza> resultList = entityManager.createQuery(query)
                .getResultList();

        return resultList;
    }

    private List<Predicate> buildAllPredicates(CriteriaBuilder cb,
                                               Root<ClientePoliza> root,
                                               String busqueda,
                                               ClientePoliza clientePoliza) {
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

        if (clientePoliza != null){
            predicates.add(cb.equal(root.get("titular"), clientePoliza));
        }
        return predicates;
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

        predicates.add(cb.equal(root.get("titular"), clientePoliza));


        return predicates;
    }


}
