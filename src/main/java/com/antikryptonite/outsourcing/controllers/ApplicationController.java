package com.antikryptonite.outsourcing.controllers;

import com.antikryptonite.outsourcing.dto.request.ApplicationRequest;
import com.antikryptonite.outsourcing.dto.response.*;
import com.antikryptonite.outsourcing.exceptions.ApplicationException;
import com.antikryptonite.outsourcing.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Контроллер заявок
 */
@RestController
@Validated
public class ApplicationController {

    private final ApplicationService applicationService;

    /**
     * Констуктор
     */
    @Autowired
    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    /**
     * Создать заявку
     */
    @PreAuthorize("hasRole('ROLE_PRODUCER')")
    @PostMapping("/purchases/{id}/applications")
    public ApplicationResponse createApplication(@PathVariable("id") UUID purchaseId, @RequestBody ApplicationRequest applicationRequest) throws
            ApplicationException {
        return applicationService.createApplication(applicationRequest, purchaseId);
    }

    /**
     * Получить заявку
     */
    @PreAuthorize("hasAnyRole('ROLE_PRODUCER', 'ROLE_ADMIN', 'ROLE_LAWYER', 'ROLE_SALESMAN')")
    @GetMapping("/purchases/{purchaseId}/applications/{applicationId}")
    public ApplicationResponse getApplication(@PathVariable("purchaseId") UUID purchaseId, @PathVariable("applicationId") UUID applicationId)
            throws ApplicationException {
        return applicationService.getApplication(applicationId, purchaseId);
    }

    /**
     * Получить все заявки
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_LAWYER', 'ROLE_SALESMAN')")
    @GetMapping("/purchases/{purchaseId}/applications")
    public List<ListApplicationResponse> getApplications(
            @PathVariable("purchaseId") UUID purchaseId, @RequestParam("kind") ApplicationType applicationType,
            @RequestParam(value = "limit", required = false, defaultValue = "50") Integer limit,
            @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset
    )
            throws ApplicationException {
        return applicationService.getApplications(purchaseId, applicationType, limit, offset);
    }

    /**
     * Получить все заявки одного поставщика по userId
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_LAWYER', 'ROLE_SALESMAN') " +
            "or hasRole('ROLE_PRODUCER') and #id.toString() == authentication.principal.username")
    @GetMapping("/producers/{id}/applications")
    public List<ApplicationProducerResponse> getProducerApplications(@PathVariable("id") UUID id) throws ApplicationException {
        return applicationService.getProducerApplications(id);
    }

}
