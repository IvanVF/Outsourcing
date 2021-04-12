package com.antikryptonite.outsourcing.repositories;

import com.antikryptonite.outsourcing.entities.WorkerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Репозиторий работника
 */
@Repository
public interface WorkerRepository extends JpaRepository<WorkerEntity, UUID> {

    /**
     * Найти сотрудника по id пользователя
     * @param id - id пользователя
     * @return - возвращает найденного пользователя
     */
    Optional<WorkerEntity> findByUserId(UUID id);

}
