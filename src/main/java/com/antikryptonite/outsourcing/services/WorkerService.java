package com.antikryptonite.outsourcing.services;

import com.antikryptonite.outsourcing.dto.request.*;
import com.antikryptonite.outsourcing.dto.response.*;
import com.antikryptonite.outsourcing.entities.*;
import com.antikryptonite.outsourcing.exceptions.*;
import com.antikryptonite.outsourcing.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис сотрудников
 */
@Service
public class WorkerService {

    private final WorkerRepository workerRepository;

    private final UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    /**
     * Конструктор сервиса
     */
    @Autowired
    public WorkerService(WorkerRepository workerRepository, UserRepository userRepository) {
        this.workerRepository = workerRepository;
        this.userRepository = userRepository;
    }

    /**
     * Сеттер PasswordEncoder
     */
    @Autowired
    public void setPasswordEncoder(@Lazy PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Метод для получения данных сотрудника
     * @param id - id сотрудника
     * @return - возвращает данные сотрудника
     */
    public WorkerResponse showWorker(UUID id) throws ResourceNotFoundException {
        UserEntity admin = userRepository.findByRole(Role.ADMIN).orElseThrow(IllegalStateException::new);
        if (id.equals(admin.getId())) {
            WorkerResponse adminResponse = new WorkerResponse();
            adminResponse.setId(id);
            adminResponse.setLogin(admin.getLogin());
            adminResponse.setRole(Role.ADMIN.toString());
            return adminResponse;
        }
        WorkerEntity workerEntity = workerRepository.findByUserId(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Worker %s", id)));
        return convertToWorkerResponse(workerEntity);
    }

    /**
     * Метод для получения списка всех сотрудников
     * @return - возвращает список всех сотрудников
     */
    public WorkersResponse showWorkers() {
        List<WorkerEntity> workersAll = workerRepository.findAll();
        List<WorkerResponse> workers = workersAll.stream().map(WorkerService::convertToWorkerResponse).collect(Collectors.toList());
        WorkersResponse workersResponse = new WorkersResponse();
        workersResponse.setWorkers(workers);
        return workersResponse;
    }

    /**
     * Метод для сохранения сотрудника в базе данных
     * @param registrationRequest - тело с параметрами сотрудника
     * @return - возвращает данные созданного сотрудника
     */
    public WorkerResponse saveWorker(RegistrationWorkerRequest registrationRequest) throws UniqueException {
        if (userRepository.findByLogin(registrationRequest.getLogin()).isPresent()) {
            throw new UniqueException("User with this email already exists");
        }
        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setLogin(registrationRequest.getLogin());
        user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        if (registrationRequest.getType() == Role.LAWYER) {
            user.setRole(Role.LAWYER);
        } else {
            user.setRole(Role.SALESMAN);
        }

        WorkerEntity workerEntity = new WorkerEntity();
        workerEntity.setWorkerId(UUID.randomUUID());
        workerEntity.setFirstName(registrationRequest.getFirstName());
        workerEntity.setLastName(registrationRequest.getLastName());
        workerEntity.setMiddleName(registrationRequest.getMiddleName());
        workerEntity.setUser(user);

        userRepository.save(user);
        workerRepository.save(workerEntity);
        return convertToWorkerResponse(workerEntity);
    }

    /**
     * Смена пароля администратором
     * @param id - id пользователя
     * @param request - запрос с новым паролем
     */
    public void changePassword(UUID id, ChangePasswordAdminRequest request) throws ResourceNotFoundException {
        UserEntity userEntity = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("User %s", id)));
        userEntity.setPassword(passwordEncoder.encode(request.getPasswordNew()));
        userRepository.save(userEntity);
    }

    private static WorkerResponse convertToWorkerResponse(WorkerEntity workerEntity) {
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
