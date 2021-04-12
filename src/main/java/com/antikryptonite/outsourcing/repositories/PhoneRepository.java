package com.antikryptonite.outsourcing.repositories;

import com.antikryptonite.outsourcing.entities.PhoneEntity;
import com.antikryptonite.outsourcing.entities.ProducerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Репозиторий для телефонных номеров
 */
@Repository
public interface PhoneRepository extends JpaRepository<PhoneEntity, UUID> {

    /**
     * Проверяет, существует ли такой номер телефона
     */
    boolean existsByPhone(Long phone);

    Optional<PhoneEntity> findByPhone(Long phone);
}
