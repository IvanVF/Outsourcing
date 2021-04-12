package com.antikryptonite.outsourcing.controllers;

import com.antikryptonite.outsourcing.dto.request.*;
import com.antikryptonite.outsourcing.dto.response.*;
import com.antikryptonite.outsourcing.exceptions.*;
import com.antikryptonite.outsourcing.services.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

/**
 * Контроллер сотрудников
 */
@RestController
public class WorkerController {

    private final WorkerService workerService;

    /**
     * Конструктор контроллера
     */
    @Autowired
    public WorkerController(WorkerService workerService) {
        this.workerService = workerService;
    }

    /**
     * GET-запрос на получение данных сотрудника
     * @param id - id сотрудника
     * @return - возвращает данные сотрудника
     */
    @GetMapping("/workers/{id}")
    @PreAuthorize("(hasAnyRole('ROLE_SALESMAN', 'ROLE_LAWYER') and #id.toString() == authentication.principal.username) or hasRole('ROLE_ADMIN')")
    public WorkerResponse showWorker(@PathVariable("id") UUID id) throws ApplicationException {
        return workerService.showWorker(id);
    }

    /**
     * GET-запрос на получение списка всех сотрудников
     * @return - возвращает список всех сотрудников
     */
    @GetMapping("/workers")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public WorkersResponse showWorkers() {
        return workerService.showWorkers();
    }

    /**
     * POST-запрос на создание учетной записи сотрудника
     * @param registrationRequest - тело запроса с параметрами
     * @return - возвращает данные созданного сотрудника
     */
    @PostMapping("/workers")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public WorkerResponse createWorker(@RequestBody @Valid RegistrationWorkerRequest registrationRequest) throws UniqueException {
        return workerService.saveWorker(registrationRequest);
    }

    /**
     * PUT-запрос на смену пароля
     * @param id - id пользователя
     * @param request - запрос с новым паролем
     */
    @PutMapping("/workers/{id}/newpassword")                                             //todo: поправить юрл
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)                                            //todo: неизвестно, что должно быть в ответе
    public void changePassword(@PathVariable("id") UUID id, @RequestBody @Valid ChangePasswordAdminRequest request)
            throws ResourceNotFoundException {
        workerService.changePassword(id, request);
    }
}