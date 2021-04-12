package com.antikryptonite.outsourcing.repositories;

import com.antikryptonite.outsourcing.entities.PurchaseStackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Репозиторий стека технологий для закупки
 */
@Repository
public interface PurchaseStackRepository extends JpaRepository<PurchaseStackEntity, UUID> {

    /**
     * Поиск стека технологий по id закупки
     */
    List<PurchaseStackEntity> findAllByPurchasePurchaseId(UUID id);

}
