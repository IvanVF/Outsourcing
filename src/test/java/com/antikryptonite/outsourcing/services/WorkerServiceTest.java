package com.antikryptonite.outsourcing.services;

import com.antikryptonite.outsourcing.dto.request.RegistrationWorkerRequest;
import com.antikryptonite.outsourcing.dto.response.*;
import com.antikryptonite.outsourcing.entities.*;
import com.antikryptonite.outsourcing.exceptions.*;
import com.antikryptonite.outsourcing.repositories.*;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WorkerServiceTest {
    final String login = "test@test.com";
    final String password = "test";
    final String firstName = "Test";
    final String lastName = "Testov";
    final String middleName = "Testovich";
    final UUID id = UUID.randomUUID();

    /**
     * Проверка на успешно созданного сотрудника
     */
    @Test
    public void saveWorker() throws UniqueException {
        RegistrationWorkerRequest registrationRequest = fillRegistrationWorkerRequest();

        UserEntity userEntity = fillUserEntity();
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findById(id)).thenReturn(Optional.of(userEntity));

        WorkerEntity workerEntity = fillWorkerEntity(userEntity);
        WorkerRepository workerRepository = mock(WorkerRepository.class);
        when(workerRepository.findByUserId(id)).thenReturn(Optional.of(workerEntity));

        WorkerService workerService = new WorkerService(workerRepository, userRepository);

        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        workerService.setPasswordEncoder(passwordEncoder);
        registrationRequest.setPassword(passwordEncoder.encode(password));

        WorkerResponse workerResponse = workerService.saveWorker(registrationRequest);
        workerResponse.setId(id);

        WorkerResponse fakeResponse = fillWorkerResponse();

        assertEquals(workerResponse, fakeResponse);
    }

    /**
     * Проверка на уникальность (когда в базе уже есть сотрудник с таким же логином)
     */
    @Test
    public void saveWorkerException() throws UniqueException {

        RegistrationWorkerRequest registrationRequest = fillRegistrationWorkerRequest();

        UserEntity userEntity = fillUserEntity();
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findById(id)).thenReturn(Optional.of(userEntity));

        WorkerEntity workerEntity = fillWorkerEntity(userEntity);
        WorkerRepository workerRepository = mock(WorkerRepository.class);
        when(workerRepository.findByUserId(id)).thenReturn(Optional.of(workerEntity));

        WorkerService workerService = new WorkerService(workerRepository, userRepository);

        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        workerService.setPasswordEncoder(passwordEncoder);
        registrationRequest.setPassword(passwordEncoder.encode(password));

        WorkerResponse workerResponse = workerService.saveWorker(registrationRequest);
        workerResponse.setId(id);

        when(userRepository.findByLogin(login)).thenReturn(Optional.of(userEntity));

        assertThrows(UniqueException.class, () -> workerService.saveWorker(registrationRequest));
    }

    /**
     * Проверка на случай отсутствия администратора в БД
     */
    @Test
    public void showWorkerAdminException() {
        UserEntity userEntity = fillUserEntity();
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findById(id)).thenReturn(Optional.of(userEntity));

        WorkerEntity workerEntity = fillWorkerEntity(userEntity);
        WorkerRepository workerRepository = mock(WorkerRepository.class);
        when(workerRepository.findByUserId(id)).thenReturn(Optional.of(workerEntity));

        WorkerService workerService = new WorkerService(workerRepository, userRepository);

        assertThrows(IllegalStateException.class, () -> workerService.showWorker(id));
    }

    /**
     * Проверка на случай отсутствия сотрудника с таким id в БД
     */
    @Test
    public void showWorkerException() {
        UserRepository userRepository = mock(UserRepository.class);
        WorkerRepository workerRepository = mock(WorkerRepository.class);
        WorkerService workerService = new WorkerService(workerRepository, userRepository);

        UserEntity adminEntity = new UserEntity();
        adminEntity.setLogin("admin");
        adminEntity.setId(UUID.randomUUID());
        adminEntity.setRole(Role.ADMIN);
        when(userRepository.findByRole(Role.ADMIN)).thenReturn(Optional.of(adminEntity));

        assertThrows(ResourceNotFoundException.class, () -> workerService.showWorker(id));
    }

    /**
     * Проверка на успешное отображение данных сотрудника
     */
    @Test
    public void showWorker() throws ResourceNotFoundException {
        UserEntity userEntity = fillUserEntity();
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findById(id)).thenReturn(Optional.of(userEntity));

        WorkerEntity workerEntity = fillWorkerEntity(userEntity);
        WorkerRepository workerRepository = mock(WorkerRepository.class);
        when(workerRepository.findByUserId(id)).thenReturn(Optional.of(workerEntity));

        WorkerService workerService = new WorkerService(workerRepository, userRepository);

        WorkerResponse duplicateResponse = fillWorkerResponse();

        UserEntity adminEntity = new UserEntity();
        adminEntity.setLogin("admin");
        adminEntity.setId(UUID.randomUUID());
        adminEntity.setRole(Role.ADMIN);
        when(userRepository.findByRole(Role.ADMIN)).thenReturn(Optional.of(adminEntity));

        assertEquals(workerService.showWorker(id), duplicateResponse);
    }

    /**
     * Проверка на успешное получение списка сотрудников
     */
    @Test
    public void showWorkers() {
        UserEntity userEntity = fillUserEntity();
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findById(id)).thenReturn(Optional.of(userEntity));

        WorkerEntity workerEntity = fillWorkerEntity(userEntity);
        WorkerRepository workerRepository = mock(WorkerRepository.class);
        when(workerRepository.findByUserId(id)).thenReturn(Optional.of(workerEntity));

        WorkerService workerService = new WorkerService(workerRepository, userRepository);

        List<WorkerEntity> workersAll = workerRepository.findAll();
        List<WorkerResponse> workers = workersAll.stream().map(WorkerServiceTest::converter).collect(Collectors.toList());
        WorkersResponse duplicateResponse = new WorkersResponse();
        duplicateResponse.setWorkers(workers);

        assertEquals(workerService.showWorkers(), duplicateResponse);
    }

    private UserEntity fillUserEntity() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(id);
        userEntity.setLogin(login);
        userEntity.setPassword(password);
        userEntity.setRole(Role.SALESMAN);
        return userEntity;
    }

    private WorkerEntity fillWorkerEntity(UserEntity user) {
        WorkerEntity workerEntity = new WorkerEntity();
        workerEntity.setWorkerId(id);
        workerEntity.setFirstName(firstName);
        workerEntity.setLastName(lastName);
        workerEntity.setMiddleName(middleName);
        workerEntity.setUser(user);
        return workerEntity;
    }

    private RegistrationWorkerRequest fillRegistrationWorkerRequest() {
        RegistrationWorkerRequest registrationRequest = new RegistrationWorkerRequest();
        registrationRequest.setLogin(login);
        registrationRequest.setFirstName(firstName);
        registrationRequest.setLastName(lastName);
        registrationRequest.setMiddleName(middleName);
        registrationRequest.setType(Role.SALESMAN);
        return registrationRequest;
    }

    private WorkerResponse fillWorkerResponse() {
        WorkerResponse workerResponse = new WorkerResponse();
        workerResponse.setId(id);
        workerResponse.setLogin(login);
        workerResponse.setFirstName(firstName);
        workerResponse.setLastName(lastName);
        workerResponse.setMiddleName(middleName);
        workerResponse.setRole(Role.SALESMAN.toString());
        return workerResponse;
    }

    private static WorkerResponse converter(WorkerEntity workerEntity) {
        WorkerResponse workerResponse = new WorkerResponse();
        workerResponse.setId(workerEntity.getUser().getId());
        workerResponse.setLogin(workerEntity.getUser().getLogin());
        workerResponse.setFirstName(workerEntity.getFirstName());
        workerResponse.setLastName(workerEntity.getLastName());
        workerResponse.setMiddleName(workerEntity.getMiddleName());
        workerResponse.setRole(workerEntity.getUser().getRole().toString());
        return workerResponse;
    }
}