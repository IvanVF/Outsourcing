package com.antikryptonite.outsourcing.repositories;

import com.antikryptonite.outsourcing.entities.ApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Репозиторий для заявок
 */
@Repository
public interface ApplicationRepository extends JpaRepository<ApplicationEntity, UUID> {

    /**
     * Поиск всех заявок по id закупки
     */
    List<ApplicationEntity> findAllByPurchasePurchaseId(UUID id);

    /**
     * Поиск всех заявок по id поставщика
     */
    List<ApplicationEntity> findAllByProducerProducerId(UUID id);

}
