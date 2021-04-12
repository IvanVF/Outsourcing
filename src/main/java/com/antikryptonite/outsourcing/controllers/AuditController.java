package com.antikryptonite.outsourcing.controllers;

import com.antikryptonite.outsourcing.controllers.enums.ActionsSort;
import com.antikryptonite.outsourcing.entities.AuditFullResponse;
import com.antikryptonite.outsourcing.entities.Role;
import com.antikryptonite.outsourcing.services.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * Контроллер для работы с историей действий
 */
@RestController
public class AuditController {

    private final AuditService auditService;

    /**
     * Конструктор
     */
    @Autowired
    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    /**
     * GET-запрос на получение истории действий по должности (по умолчанию - все)
     */
    @GetMapping("/actions")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public AuditFullResponse showAudit(
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-d") LocalDate date,
            @RequestParam(value = "sort", required = false, defaultValue = "TIME") ActionsSort sort
    ) {
        return auditService.showAudit(role, date, sort);
    }
}
