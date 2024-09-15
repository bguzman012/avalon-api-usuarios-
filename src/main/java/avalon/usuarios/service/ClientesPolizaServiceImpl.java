package avalon.usuarios.service;

import avalon.usuarios.data.ClientePolizaRepository;
import avalon.usuarios.data.ClienteRepository;
import avalon.usuarios.data.PolizaRepository;
import avalon.usuarios.data.UsuarioRepository;
import avalon.usuarios.model.pojo.*;
import avalon.usuarios.model.request.MigracionClientePolizaRequest;
import avalon.usuarios.model.request.NotificacionRequest;
import avalon.usuarios.model.response.MigracionResponse;
import avalon.usuarios.service.externos.NotificacionService;
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
import org.aspectj.weaver.loadtime.Agent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@Service
public class ClientesPolizaServiceImpl implements ClientesPolizaService {

    private final String ROL_CLIENTE = "CLI";
    private final String ROL_ASESOR = "ASR";
    private final String ROL_AGENTE = "BRO";
    private final ClientePolizaRepository repository;
    @Autowired
    private NotificacionService notificacionService;

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
    public void enviarNotificacionesMiembrosClientePolizas(ClientePoliza clientePoliza, String asunto, String mensaje, Usuario usuario) {

        if (!usuario.getRol().getCodigo().equals(this.ROL_CLIENTE)) {
            NotificacionRequest notificacionRequestCliente = new NotificacionRequest();
            notificacionRequestCliente.setAsunto(asunto);
            notificacionRequestCliente.setMensaje(mensaje);
            notificacionRequestCliente.setUsuarioEnvia(usuario.getNombreUsuario());

            // Se  envia al titular, el cliente es menor de edad
            if (!clientePoliza.getCliente().tiene18OMasAnios())
                notificacionRequestCliente.setUsuarioAseguradorId(clientePoliza.getTitular().getCliente().getNombreUsuario());
            else
                notificacionRequestCliente.setUsuarioAseguradorId(clientePoliza.getCliente().getNombreUsuario());

            notificacionRequestCliente.setTipoNotificacionId(1L);

            notificacionService.enviarNotificacion(notificacionRequestCliente);
        }

        if (!usuario.getRol().getCodigo().equals(this.ROL_AGENTE)) {
            NotificacionRequest notificacionRequestAgente = new NotificacionRequest();

            notificacionRequestAgente.setAsunto(asunto);
            notificacionRequestAgente.setMensaje(mensaje);
            notificacionRequestAgente.setUsuarioEnvia(usuario.getNombreUsuario());
            notificacionRequestAgente.setUsuarioAseguradorId(clientePoliza.getAgente().getNombreUsuario());
            notificacionRequestAgente.setTipoNotificacionId(1L);

            notificacionService.enviarNotificacion(notificacionRequestAgente);
        }

        if (!usuario.getRol().getCodigo().equals(this.ROL_ASESOR)) {
            NotificacionRequest notificacionRequestAsesor = new NotificacionRequest();

            notificacionRequestAsesor.setAsunto(asunto);
            notificacionRequestAsesor.setMensaje(mensaje);
            notificacionRequestAsesor.setUsuarioEnvia(usuario.getNombreUsuario());
            notificacionRequestAsesor.setUsuarioAseguradorId(clientePoliza.getAsesor().getNombreUsuario());
            notificacionRequestAsesor.setTipoNotificacionId(1L);

            notificacionService.enviarNotificacion(notificacionRequestAsesor);
        }
    }

    @Override
    public MigracionResponse saveMigracionClientePoliza(MigracionClientePolizaRequest request) {

        return null;
    }

    @Override
    public Page<ClientePoliza> searchClienesPolizas(String busqueda, Pageable pageable, Cliente cliente, Poliza poliza, Usuario usuario) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Consulta principal para los resultados paginados
        CriteriaQuery<ClientePoliza> query = cb.createQuery(ClientePoliza.class);
        Root<ClientePoliza> cmRoot = query.from(ClientePoliza.class);

        List<Predicate> predicates = buildPredicates(cb, cmRoot, busqueda, cliente, poliza, usuario);

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        Sort.Order sortOrder = pageable.getSort().iterator().next();
        Order order = sortOrder.isAscending() ? cb.asc(cmRoot.get(sortOrder.getProperty())) : cb.desc(cmRoot.get(sortOrder.getProperty()));
        query.orderBy(order);

        List<ClientePoliza> resultList = entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long totalRecords = countClientesPolizas(busqueda, cliente, poliza, usuario);

        return new PageImpl<>(resultList, pageable, totalRecords);
    }

    @Override
    public List<ClientePoliza> searchAllClienesPolizas(String busqueda, String sortField, String sortOrder) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ClientePoliza> query = cb.createQuery(ClientePoliza.class);
        Root<ClientePoliza> cmRoot = query.from(ClientePoliza.class);

        List<Predicate> predicates = buildAllPredicates(cb, cmRoot, busqueda);

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        Order order;
        if ("asc".equalsIgnoreCase(sortOrder)) {
            order = cb.asc(cmRoot.get(sortField));
        } else {
            order = cb.desc(cmRoot.get(sortField));
        }
        query.orderBy(order);

        List<ClientePoliza> resultList = entityManager.createQuery(query)
                .getResultList();

        return resultList;
    }

    @Override
    public Boolean existClientePolizaTitular(String numeroCertificado, String tipo) {
        return this.repository.existsByNumeroCertificadoAndTipoAndEstado(numeroCertificado, tipo, "A");
    }

    @Override
    public ByteArrayOutputStream generateExcelClientesPolizas(String busqueda, String sortField, String sortOrder) throws IOException {
        List<ClientePoliza> allClientesPolizas = this.searchAllClienesPolizas(busqueda, sortField, sortOrder);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Clientes");

        Row headerRow = sheet.createRow(0);
        Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue("#");

        headerCell = headerRow.createCell(1);
        headerCell.setCellValue("NUM. CERT.");

        headerCell = headerRow.createCell(2);
        headerCell.setCellValue("CLIENTE");

        headerCell = headerRow.createCell(3);
        headerCell.setCellValue("ASEGURADORA");

        headerCell = headerRow.createCell(4);
        headerCell.setCellValue("POLIZA");

        headerCell = headerRow.createCell(5);
        headerCell.setCellValue("EMPRESA");

        headerCell = headerRow.createCell(6);
        headerCell.setCellValue("ASESOR");

        headerCell = headerRow.createCell(7);
        headerCell.setCellValue("AGENTE");

        int rowNum = 1;
        int registro = 1;
        for (ClientePoliza clientePoliza : allClientesPolizas) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue((int) registro);
            row.createCell(1).setCellValue(clientePoliza.getNumeroCertificado());
            row.createCell(2).setCellValue(clientePoliza.getCliente().getNombreUsuario());
            row.createCell(3).setCellValue(clientePoliza.getPoliza().getAseguradora().getNombre());
            row.createCell(4).setCellValue(clientePoliza.getPoliza().getNombre());
            row.createCell(5).setCellValue(clientePoliza.getEmpresa() != null ? clientePoliza.getEmpresa().getNombre() : "");
            row.createCell(6).setCellValue(clientePoliza.getAsesor().getNombreUsuario());
            row.createCell(7).setCellValue(clientePoliza.getAgente().getNombreUsuario());

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

    private Long countClientesPolizas(String busqueda, Cliente cliente, Poliza poliza, Usuario usuario) {
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

        // Agregar un LEFT JOIN explícito para 'empresa'
        Join<ClientePoliza, Empresa> empresaJoin = cmRoot.join("empresa", JoinType.LEFT);

        if (busqueda != null && !busqueda.isEmpty()) {
            String likePattern = "%" + busqueda.toLowerCase() + "%";

            predicates.add(cb.or(
                    cb.like(cb.lower(cmRoot.get("cliente").get("nombreUsuario")), likePattern),
                    cb.like(cb.lower(cmRoot.get("asesor").get("nombreUsuario")), likePattern),
                    cb.like(cb.lower(cmRoot.get("agente").get("nombreUsuario")), likePattern),
                    cb.like(cb.lower(cmRoot.get("poliza").get("nombre")), likePattern),
                    cb.like(cb.lower(cmRoot.get("poliza").get("aseguradora").get("nombre")), likePattern),
                    cb.like(cb.lower(empresaJoin.get("nombre")), likePattern), // Usar el 'join' aquí
                    cb.like(cb.lower(cmRoot.get("codigo")), likePattern),
                    cb.like(cb.lower(cmRoot.get("numeroCertificado")), likePattern),
                    cb.like(cb.function("TO_CHAR", String.class, cmRoot.get("fechaInicio"), cb.literal("yyyy-MM-dd")), likePattern),
                    cb.like(cb.function("TO_CHAR", String.class, cmRoot.get("fechaFin"), cb.literal("yyyy-MM-dd")), likePattern)
            ));
        }

        // Si se esta consultando las polizas por cliente y si el usuario loggeado es cliente
        if (usuario.getRol().getCodigo().equals(this.ROL_CLIENTE) && cliente != null) {
            // Si el cliente loggeado es diferente al cliente que se esta consultando
            if (!Objects.equals(usuario.getId(), cliente.getId())) {
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

    private List<Predicate> buildAllPredicates(CriteriaBuilder cb,
                                               Root<ClientePoliza> cmRoot,
                                               String busqueda) {
        List<Predicate> predicates = new ArrayList<>();

        // Agregar un LEFT JOIN explícito para 'empresa'
        Join<ClientePoliza, Empresa> empresaJoin = cmRoot.join("empresa", JoinType.LEFT);

        if (busqueda != null && !busqueda.isEmpty()) {
            String likePattern = "%" + busqueda.toLowerCase() + "%";

            predicates.add(cb.or(
                    cb.like(cb.lower(cmRoot.get("cliente").get("nombreUsuario")), likePattern),
                    cb.like(cb.lower(cmRoot.get("asesor").get("nombreUsuario")), likePattern),
                    cb.like(cb.lower(cmRoot.get("agente").get("nombreUsuario")), likePattern),
                    cb.like(cb.lower(cmRoot.get("poliza").get("nombre")), likePattern),
                    cb.like(cb.lower(cmRoot.get("poliza").get("aseguradora").get("nombre")), likePattern),
                    cb.like(cb.lower(empresaJoin.get("nombre")), likePattern), // Usar el 'join' aquí
                    cb.like(cb.lower(cmRoot.get("codigo")), likePattern),
                    cb.like(cb.lower(cmRoot.get("numeroCertificado")), likePattern),
                    cb.like(cb.function("TO_CHAR", String.class, cmRoot.get("fechaInicio"), cb.literal("yyyy-MM-dd")), likePattern),
                    cb.like(cb.function("TO_CHAR", String.class, cmRoot.get("fechaFin"), cb.literal("yyyy-MM-dd")), likePattern)
            ));
        }
        return predicates;
    }

    @Override
    public Optional<ClientePoliza> getClientePoliza(Long clientePolizaId) {
        return this.repository.findById(clientePolizaId);
    }

    @Override
    public Optional<ClientePoliza> getClientePolizaTitularByCertificado(String numeroCertificado) {
        return this.repository.findByNumeroCertificadoAndTipo(numeroCertificado, "TITULAR");
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
        ClientePoliza clientePoliza = this.getClientePoliza(clientePolizaId).orElseThrow(() -> new IllegalArgumentException("Cliente Poliza no encontrada"));
        ;
        this.repository.delete(clientePoliza);
    }

    // Ejecutar cada 10 minutos
    @Scheduled(cron = "0 0 0 * * ?")  // Ejemplo: se ejecuta a la medianoche todos los días
    @Transactional
    public void actualizarPolizasVencidas() {
        cambiarEstadoPolizasVencidas();
    }

    // Ejecutar al iniciar la aplicación
    @PostConstruct
    public void actualizarPolizasAlIniciar() {
        cambiarEstadoPolizasVencidas();
    }

    private void cambiarEstadoPolizasVencidas() {
        List<ClientePoliza> clientePolizas = repository.findAllByEstado("A");

        Date hoy = new Date();
        for (ClientePoliza clientePoliza : clientePolizas) {
            if (clientePoliza.getFechaFin() != null && clientePoliza.getFechaFin().before(hoy)) {
                clientePoliza.setEstado("V");
                repository.save(clientePoliza);
            }
        }
    }
}
