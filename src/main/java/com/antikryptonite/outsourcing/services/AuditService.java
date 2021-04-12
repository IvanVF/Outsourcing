package com.antikryptonite.outsourcing.services;

import com.antikryptonite.outsourcing.controllers.enums.ActionsSort;
import com.antikryptonite.outsourcing.dto.response.AuditResponse;
import com.antikryptonite.outsourcing.entities.*;
import com.antikryptonite.outsourcing.exceptions.ResourceNotFoundException;
import com.antikryptonite.outsourcing.repositories.*;
import com.antikryptonite.outsourcing.services.paging.OffsetLimitRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис истории действий
 */
@Service
@Transactional
public class AuditService {

    private final UserRepository userRepository;

    private final ProducerRepository producerRepository;

    private final EntityProducerRepository entityProducerRepository;

    private final PhysicalProducerRepository physicalProducerRepository;

    private final WorkerRepository workerRepository;

    private final AuditRepository auditRepository;

    /**
     * Конструктор сервиса
     */
    @Autowired
    public AuditService(UserRepository userRepository,
                        ProducerRepository producerRepository,
                        EntityProducerRepository entityProducerRepository,
                        PhysicalProducerRepository physicalProducerRepository,
                        WorkerRepository workerRepository,
                        AuditRepository auditRepository) {
        this.userRepository = userRepository;
        this.producerRepository = producerRepository;
        this.entityProducerRepository = entityProducerRepository;
        this.physicalProducerRepository = physicalProducerRepository;
        this.workerRepository = workerRepository;
        this.auditRepository = auditRepository;
    }

    /**
     * Метод добавляет запись о действии в БД
     */
    public void recordAction(UUID userId, String action) throws ResourceNotFoundException {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(String.format("User %s", userId)));

        AuditEntity auditEntity = new AuditEntity();
        auditEntity.setActionTime(LocalDateTime.now());
        auditEntity.setRole(userEntity.getRole());
        auditEntity.setAction(action);
        auditEntity.setUser(userEntity);
        auditEntity.setId(UUID.randomUUID());

        if (userEntity.getRole().equals(Role.ADMIN)) {
            auditEntity.setFirstName("Администратор");
            auditEntity.setLastName("Администратор");
            auditEntity.setMiddleName("Администратор");
        } else {
            WorkerEntity workerEntity = workerRepository.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException(String.format("Worker %s", userId)));
            auditEntity.setFirstName(workerEntity.getFirstName());
            auditEntity.setLastName(workerEntity.getLastName());
            auditEntity.setMiddleName(workerEntity.getMiddleName());
        }

        auditRepository.save(auditEntity);
    }

    /**
     * Метод формирует название организации для ЮЛ или ФИО для ФЛ
     */
    public String createOrgName(UUID userId) throws ResourceNotFoundException {
        String orgName = "";
        ProducerEntity producerEntity = producerRepository.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException(String.format("Producer %s", userId)));
        if (producerEntity.isIndividual()) {
            IndividualProducerEntity individualProducerEntity = physicalProducerRepository.getByProducer(producerEntity).orElseThrow(() -> new ResourceNotFoundException(String.format("Producer %s", userId)));
            orgName = individualProducerEntity.getFirstName() + " " + individualProducerEntity.getLastName() + " " + individualProducerEntity.getMiddleName();
        } else {
            EntityProducerEntity entityProducerEntity = entityProducerRepository.getByProducer(producerEntity).orElseThrow(() -> new ResourceNotFoundException(String.format("Producer %s", userId)));
            orgName = entityProducerEntity.getOrganizationName();
        }
        return orgName;
    }

    /**
     * Метод возвращает историю дейстивий
     */
    public AuditFullResponse showAudit(Role role, LocalDate date, ActionsSort actionsSort) {
        Sort sort;
        if (actionsSort == ActionsSort.TIME) {
            sort = Sort.by("actionTime").descending();
        } else {
            sort = Sort.by("lastName").ascending().and(Sort.by("firstName").ascending()).and(Sort.by("middleName").ascending());
        }
        List<AuditEntity> auditEntityList = auditRepository.findAll(sort);

       if (role == Role.ADMIN) {
           auditEntityList = auditEntityList.stream().filter(auditEntity -> auditEntity.getRole().equals(Role.ADMIN)).collect(Collectors.toList());
        }
        if (role == Role.SALESMAN) {
            auditEntityList = auditEntityList.stream().filter(auditEntity -> auditEntity.getRole().equals(Role.SALESMAN)).collect(Collectors.toList());
        }
        if (role == Role.LAWYER) {
            auditEntityList = auditEntityList.stream().filter(auditEntity -> auditEntity.getRole().equals(Role.LAWYER)).collect(Collectors.toList());
        }

        if (date != null) {
            auditEntityList = auditEntityList.stream().filter(auditEntity -> auditEntity.getActionTime().toLocalDate().equals(date)).collect(Collectors.toList());
        }

        List<AuditResponse> auditResponseList = auditEntityList.stream().map(this::convertToAuditResponse).collect(Collectors.toList());
        AuditFullResponse auditFullResponse = new AuditFullResponse();
        auditFullResponse.setActions(auditResponseList);
        if (role == null) {
            auditFullResponse.setTotalLength(auditRepository.count());
        } else {
            auditFullResponse.setTotalLength(auditRepository.countByRole(role));
        }

        return auditFullResponse;
    }

    private AuditResponse convertToAuditResponse(AuditEntity auditEntity) {
        AuditResponse auditResponse = new AuditResponse();

        auditResponse.setAction(auditEntity.getAction());
        auditResponse.setActionTime(auditEntity.getActionTime());
        auditResponse.setFirstName(auditEntity.getFirstName());
        auditResponse.setLastName(auditEntity.getLastName());
        auditResponse.setMiddleName(auditEntity.getMiddleName());
        auditResponse.setRole(auditEntity.getRole());

        return auditResponse;
    }

}
