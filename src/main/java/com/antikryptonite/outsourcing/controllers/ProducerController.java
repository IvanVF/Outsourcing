package com.antikryptonite.outsourcing.controllers;

import com.antikryptonite.outsourcing.controllers.enums.State;
import com.antikryptonite.outsourcing.dto.request.*;
import com.antikryptonite.outsourcing.dto.response.*;
import com.antikryptonite.outsourcing.exceptions.*;
import com.antikryptonite.outsourcing.services.AuditService;
import com.antikryptonite.outsourcing.services.ProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

/**
 * Контроллер поставщика
 */
@RestController
@RequestMapping("/producers")
public class ProducerController {

    private final ProducerService producerService;
    private final AuditService auditService;

    /**
     * Конструктор поставщика
     */
    @Autowired
    public ProducerController(ProducerService producerService, AuditService auditService) {
        this.producerService = producerService;
        this.auditService = auditService;
    }

    /**
     * GET-запрос на получение списка всех поставщиков
     *
     * @return - возвращает список всех поставщиков
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_LAWYER', 'ROLE_SALESMAN')")
    public ProducersResponse showProducers(
            @RequestParam(required = false) State state,
            @RequestParam(required = false) String stack
    ) {
        return producerService.showProducers(state, stack);
    }

    /**
     * GET-запрос на получение одного поставщика
     *
     * @return - возвращает одного поставщика
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_LAWYER', 'ROLE_SALESMAN') or (hasAnyRole('ROLE_PRODUCER', 'ROLE_USER') and #id.toString() == authentication.principal.username)")
    public ProducerFullResponse showOneProducer(@PathVariable("id") UUID id) throws ResourceNotFoundException, IllegalArgumentException {
        return producerService.showOneProducer(id);
    }

    /**
     * PUT-запрос устанавливает аккредитацию поставщика на true
     */
    @PutMapping("/{id}/accreditation")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_LAWYER')")
    public void accreditProducer(
            @PathVariable(value = "id") UUID id
    ) throws ResourceNotFoundException {
        producerService.accreditProducer(id);
    }

    /**
     * PUT-запрос подать заявку на аккредитацию юридическому лицу
     */
    @PutMapping(value = "{id}/apply")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_USER') and #id.toString() == authentication.principal.username")
    public void callForProducerAccreditation(
            @PathVariable(value = "id") UUID id, @RequestBody @Valid ApplyEntityRequest applyEntityRequest
    ) throws ApplicationException {
        producerService.callForEntityProducerAccreditation(id, applyEntityRequest);
    }

    /**
     * PUT-запрос подать заявку на аккредитацию физическому лицу
     */
    @PutMapping(value = "{id}/apply", params = {"isIndividual"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_USER') and #id.toString() == authentication.principal.username")
    public void callForProducerAccreditation(
            @PathVariable(value = "id") UUID id,
            @RequestParam boolean isIndividual,
            @RequestBody @Valid ApplyIndividualRequest applyIndividualRequest
    ) throws ApplicationException {
        producerService.callForProducerIndividualAccreditation(id, applyIndividualRequest);
    }

    /**
     * POST-запрос на добавление поставщика в черный список
     *
     * @param userId - id поставщика
     */
    @PutMapping("{id}/block")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_LAWYER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addToBlackList(@PathVariable("id") UUID userId, @RequestBody @Valid BlockRequest blockRequest) throws ResourceNotFoundException {
        producerService.addToBlackList(userId, blockRequest);
    }

    /**
     * POST-запрос на удаление поставщика из черного списка
     *
     * @param userId - id поставщика
     */
    @PutMapping("{id}/unblock")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void outFromBlackList(@PathVariable("id") UUID userId, @RequestBody @Valid BlockRequest blockRequest) throws ResourceNotFoundException {
        producerService.outFromBlackList(userId, blockRequest);
    }
}
