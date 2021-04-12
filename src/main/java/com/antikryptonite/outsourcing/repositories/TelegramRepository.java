package com.antikryptonite.outsourcing.repositories;

import com.antikryptonite.outsourcing.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Репозиторий для записей о Telegram
 */
@Repository
public interface TelegramRepository extends JpaRepository<TelegramEntity, UUID> {

    /**
     * Проверяет, существует ли уже данный id в системе для данного поставщика
     */
    boolean existsByTelegramIdAndProducer(Integer telegramId, ProducerEntity producer);
}
