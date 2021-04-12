package com.antikryptonite.outsourcing.controllers;

import com.antikryptonite.outsourcing.dto.request.*;
import com.antikryptonite.outsourcing.dto.response.*;
import com.antikryptonite.outsourcing.entities.StatusPurchase;
import com.antikryptonite.outsourcing.exceptions.*;
import com.antikryptonite.outsourcing.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

/**
 * Контроллер закупок
 */
@RestController
public class PurchaseController {

    private final PurchaseService purchaseService;

    private final AuditService auditService;

    /**
     * Конструктор
     */
    @Autowired
    public PurchaseController(PurchaseService purchaseService, AuditService auditService) {
        this.purchaseService = purchaseService;
        this.auditService = auditService;
    }

    /**
     * Создать закупку
     */
    @PostMapping("/purchases")
    @PreAuthorize("hasAnyRole('ROLE_SALESMAN', 'ROLE_ADMIN')")
    public PurchaseCreateResponse createPurchase(@RequestBody @Valid PurchaseRequest purchaseRequest) throws ResourceNotFoundException, UniqueException {
        return purchaseService.createPurchase(purchaseRequest);
    }

    /**
     * Показать одну закупку
     */
    @GetMapping("/purchases/{id}")
    public PurchaseResponse showPurchase(@PathVariable("id") UUID id) throws ResourceNotFoundException {
        return purchaseService.showPurchase(id);
    }

    /**
     * Показать закупки
     */
    @GetMapping("/purchases")
    public List<PurchaseListResponse> showPurchases(
            @RequestParam(value = "status", required = false) StatusPurchase status,
            @RequestParam(value = "limit", required = false, defaultValue = "50") Integer limit,
            @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset) {
        return purchaseService.showPurchases(status, limit, offset);
    }

    /**
     * Закрыть закупки
     */
    @PutMapping("/purchases/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ROLE_SALESMAN', 'ROLE_ADMIN')")
    public void closePurchase(@PathVariable("id") UUID id, @RequestBody @Valid PurchaseCloseRequest request) throws ApplicationException {
        purchaseService.closePurchase(id, request);
    }

}