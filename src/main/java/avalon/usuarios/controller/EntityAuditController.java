package avalon.usuarios.controller;

import avalon.usuarios.model.auditing.EntityAudit;
import avalon.usuarios.model.pojo.Cobertura;
import avalon.usuarios.model.pojo.Poliza;
import avalon.usuarios.model.request.CoberturaRequest;
import avalon.usuarios.model.response.PaginatedResponse;
import avalon.usuarios.service.CoberturaService;
import avalon.usuarios.service.EntityAuditService;
import avalon.usuarios.service.PolizaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EntityAuditController {

    private final EntityAuditService service;

    @GetMapping("/entityAudits")
    public ResponseEntity<PaginatedResponse<EntityAudit>> getEntityAudits(@RequestParam(required = false) String busquedaEntityName,
                                                                          @RequestParam(required = false) String busquedaOperation,
                                                                          @RequestParam(required = false) String busquedaId,
                                                                          @RequestParam(required = false) String busquedaCreatedDate,
                                                                          @RequestParam(required = false) String busquedaUser,
                                                                          @RequestParam(defaultValue = "0") int page,
                                                                          @RequestParam(defaultValue = "10") int size,
                                                                          @RequestParam(defaultValue = "createdDate") String sortField,
                                                                          @RequestParam(defaultValue = "desc") String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("desc") ? Sort.by(sortField).descending() : Sort.by(sortField).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<EntityAudit> entityAuditPage = service.searchEntityAudits(busquedaEntityName, busquedaOperation, busquedaId,
                busquedaCreatedDate, busquedaUser, pageable);

        List<EntityAudit> entityAudits = entityAuditPage.getContent();
        long totalRecords = entityAuditPage.getTotalElements();

        PaginatedResponse<EntityAudit> response = new PaginatedResponse<>(entityAudits, totalRecords);
        return ResponseEntity.ok(response);
    }
}