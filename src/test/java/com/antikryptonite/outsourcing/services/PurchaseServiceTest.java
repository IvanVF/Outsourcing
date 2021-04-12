package com.antikryptonite.outsourcing.services;

import com.antikryptonite.outsourcing.dto.request.PurchaseRequest;
import com.antikryptonite.outsourcing.dto.response.PurchaseCreateResponse;
import com.antikryptonite.outsourcing.entities.*;
import com.antikryptonite.outsourcing.exceptions.*;
import com.antikryptonite.outsourcing.repositories.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.time.LocalDate;
import java.util.*;

public class PurchaseServiceTest {
    final String name = "закупка";
    final String description = "описание закупки";
    final String subject = "срочно";
    final Long startingPrice = 9999L;
    final CurrencyValue currency = CurrencyValue.RUB;
    final LocalDate startDate = LocalDate.parse("2002-10-02");
    final LocalDate finishDate = LocalDate.parse("2022-10-02");
    final List<String> stack = Arrays.asList("C++", "Java", "Python");


    @Test
    public void createPurchase() throws ResourceNotFoundException, UniqueException {
        PurchaseRequest purchaseRequest = fillPurchaseRequest();
        //PurchaseEntity purchaseEntity = fillPurchaseEntity(purchaseRequest);


        PurchaseEntity purchaseEntity = fillPurchaseEntity(purchaseRequest);
        PurchaseRepository purchaseRepository = mock(PurchaseRepository.class);
        when(purchaseRepository.save(Mockito.any())).thenReturn(purchaseEntity);
//        when(purchaseRepository.save(purchaseEntity).thenReturn(Optional.of(purchaseEntity));


        PurchaseStackRepository purchaseStackRepository = mock(PurchaseStackRepository.class);
        ApplicationRepository applicationRepository = mock(ApplicationRepository.class);
        DocumentRepository documentRepository = mock(DocumentRepository.class);
//        PurchaseService purchaseService = new PurchaseService(purchaseRepository, purchaseStackRepository, applicationRepository, documentRepository,
//                producerRepository
//        );

//        PurchaseCreateResponse response = purchaseService.createPurchase(purchaseRequest);


    }


    private PurchaseEntity fillPurchaseEntity(PurchaseRequest request) {
        PurchaseEntity entity = new PurchaseEntity();
        entity.setPurchaseId(UUID.randomUUID());
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setSubject(request.getSubject());
        entity.setStartingPrice(request.getStartingPrice());
        entity.setCurrency(request.getCurrency());
        entity.setStartDate(request.getStartDate());
        entity.setFinishDate(request.getFinishDate());
        entity.setPublicationDate(LocalDate.now());
        entity.setAdditionalStagePossible(request.isAdditionalStagePossible());
        entity.setStatus(calculatePurchaseStatus(entity));

        return entity;
    }

    private PurchaseRequest fillPurchaseRequest() {
        PurchaseRequest request = new PurchaseRequest();
        request.setName(name);
        request.setDescription(description);
        request.setSubject(subject);
        request.setStartingPrice(startingPrice);
        request.setCurrency(currency);
        request.setStartDate(startDate);
        request.setFinishDate(finishDate);
        request.setStack(stack);
        request.setAdditionalStagePossible(true);
        return request;
    }




    private static StatusPurchase calculatePurchaseStatus(PurchaseEntity entity) {
        if (entity.getStatus() != StatusPurchase.CANCELED && entity.getStatus() != StatusPurchase.CLOSED) {
            if ((entity.getStartDate().isBefore(LocalDate.now()) || entity.getStartDate().isEqual(LocalDate.now())) && entity.getFinishDate().isAfter(LocalDate.now())) {
                entity.setStatus(StatusPurchase.OPENED);
            }
            if (entity.getFinishDate().isBefore(LocalDate.now())) {
                entity.setStatus(StatusPurchase.FINISHED);
            }
            if (entity.getStartDate().isAfter(LocalDate.now())) {
                entity.setStatus(StatusPurchase.PUBLISHED);
            }
        }
        return entity.getStatus();
    }

}
