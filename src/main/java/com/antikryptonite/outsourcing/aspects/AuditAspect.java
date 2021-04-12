package com.antikryptonite.outsourcing.aspects;

import com.antikryptonite.outsourcing.dto.request.PurchaseCloseRequest;
import com.antikryptonite.outsourcing.dto.request.PurchaseRequest;
import com.antikryptonite.outsourcing.entities.PurchaseEntity;
import com.antikryptonite.outsourcing.entities.StatusPurchase;
import com.antikryptonite.outsourcing.exceptions.ResourceNotFoundException;
import com.antikryptonite.outsourcing.repositories.PurchaseRepository;
import com.antikryptonite.outsourcing.services.AuditService;
import lombok.extern.java.Log;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Аспект для записи в БД истории действий
 */
@Component
@Aspect
@Log
public class AuditAspect {

    private final AuditService auditService;

    private final PurchaseRepository purchaseRepository;

    /**
     * Констуктор
     */
    @Autowired
    public AuditAspect(AuditService auditService, PurchaseRepository purchaseRepository) {
        this.auditService = auditService;
        this.purchaseRepository = purchaseRepository;
    }

    /**
     * Запись в БД audit информации о аккредитовавшем поставщика сотруднике
     */
    @AfterReturning(pointcut = "execution (public void com.antikryptonite.outsourcing.services.ProducerService.accreditProducer(..))")
    public void accreditProducerAspect(JoinPoint joinPoint) throws ResourceNotFoundException {
        UUID producerUserId = (UUID) joinPoint.getArgs()[0];
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        auditService.recordAction(UUID.fromString(userDetails.getUsername()),"Аккредитовал " + auditService.createOrgName(producerUserId));
    }

    /**
     * Запись в БД audit информации о поместившем в ЧС поставщика сотруднике
     */
    @AfterReturning(pointcut = "execution (public void com.antikryptonite.outsourcing.services.ProducerService.addToBlackList(..))")
    public void addToBlackListAspect(JoinPoint joinPoint) throws ResourceNotFoundException {
        UUID producerUserId = (UUID) joinPoint.getArgs()[0];
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        auditService.recordAction(UUID.fromString(userDetails.getUsername()),"Добавил в ЧС " + auditService.createOrgName(producerUserId));
    }

    /**
     * Запись в БД audit информации о изъявшем из ЧС поставщика сотруднике
     */
    @AfterReturning(pointcut = "execution (public void com.antikryptonite.outsourcing.services.ProducerService.outFromBlackList(..))")
    public void outFromBlackListAspect(JoinPoint joinPoint) throws ResourceNotFoundException {
        UUID producerUserId = (UUID) joinPoint.getArgs()[0];
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        auditService.recordAction(UUID.fromString(userDetails.getUsername()),"Удалил из ЧС " + auditService.createOrgName(producerUserId));
    }

    /**
     * Запись в БД audit информации о создавшем заявку сотруднике
     */
    @AfterReturning(pointcut = "execution(public com.antikryptonite.outsourcing.dto.response.PurchaseCreateResponse " +
            "com.antikryptonite.outsourcing.services.PurchaseService.createPurchase(..))")
    public void createPurchaseAspect(JoinPoint joinPoint) throws ResourceNotFoundException {
        PurchaseRequest purchaseRequest = (PurchaseRequest) joinPoint.getArgs()[0];
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        auditService.recordAction(UUID.fromString(userDetails.getUsername()),"Создал закупку " + purchaseRequest.getName());
    }

    /**
     * Запись в БД audit информации о изменившем статус заявки сотруднике
     */
    @AfterReturning(pointcut = "execution (public void com.antikryptonite.outsourcing.services.PurchaseService.closePurchase(..))")
    public void closePurchaseAspect(JoinPoint joinPoint) throws ResourceNotFoundException {
        PurchaseEntity purchaseEntity = purchaseRepository.findById( (UUID) joinPoint.getArgs()[0] ).orElseThrow(() -> new ResourceNotFoundException(String.format("Purchase %s", (UUID) joinPoint.getArgs()[1])));
        PurchaseCloseRequest request = (PurchaseCloseRequest) joinPoint.getArgs()[1];
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String comment = "Комментарий не указан";

        if (request.getStatus() == StatusPurchase.FINISHED) {
            if (request.getClosingDescription() != null) { comment = request.getClosingDescription(); }
            auditService.recordAction(UUID.fromString(userDetails.getUsername()),"Завершил закупку " + purchaseEntity.getName() + ", комментарий сотрудника: " + comment);
        }
        else if (request.getStatus() == StatusPurchase.CANCELED) {
            if (request.getClosingDescription() != null) { comment = request.getClosingDescription(); }
            auditService.recordAction(UUID.fromString(userDetails.getUsername()),"Снял закупку с публикации (отменил) " + purchaseEntity.getName() +  ", комментарий сотрудника: " + comment);
        } else {
            if (request.getClosingDescription() != null) { comment = request.getClosingDescription(); }
            auditService.recordAction(UUID.fromString(userDetails.getUsername()),"Изменил статус закупки " + purchaseEntity.getName() + " на " + request.getStatus() + ", комментарий сотрудника: " + comment);
        }
    }

}
