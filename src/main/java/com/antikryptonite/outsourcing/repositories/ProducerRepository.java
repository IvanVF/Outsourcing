package com.antikryptonite.outsourcing.repositories;

import com.antikryptonite.outsourcing.entities.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Репозиторий поставщиков
 */
@Repository
public interface ProducerRepository extends JpaRepository<ProducerEntity, UUID> {

    /**
     * Поиск поставщика по его личному номеру
     *
     * @param confirmCode - личный номер пользователя
     */
    Optional<ProducerEntity> findByConfirmCode(UUID confirmCode);

    /**
     * Поиск поставщика по ИНН
     *
     * @param inn - номер ИНН
     */
    Optional<ProducerEntity> findByInn(String inn);

    /**
     * Поиск всех аккредитованных поставщиков
     */
    @Query("SELECT producer " +
            "FROM ProducerEntity producer " +
            "WHERE producer.accreditation = com.antikryptonite.outsourcing.entities.AccreditationType.ACCREDITED AND producer.request = true")
    List<ProducerEntity> getAccreditedProducers();

    /**
     * Поиск всех неаккредитованных поставщиков
     */
    @Query("SELECT producer " +
            "FROM ProducerEntity producer " +
            "WHERE producer.accreditation = com.antikryptonite.outsourcing.entities.AccreditationType.UNACCREDITED AND producer.request = true")
    List<ProducerEntity> getUnaccreditedProducers();

    /**
     * Поиск всех поставщиков в ЧС
     */
    @Query("SELECT producer " +
            "FROM ProducerEntity producer " +
            "WHERE producer.accreditation = com.antikryptonite.outsourcing.entities.AccreditationType.BLOCK")
    List<ProducerEntity> getBlockProducers();

    /**
     * Поиск всех поставщиков, кроме ЧС
     */
    @Query("SELECT producer " +
            "FROM ProducerEntity producer " +
            "WHERE producer.accreditation <> com.antikryptonite.outsourcing.entities.AccreditationType.BLOCK AND producer.request = true")
    List<ProducerEntity> getUnblockProducers();

    /**
     * Поиск всех поставщиков, которые не подали заявки и не в ЧС
     */
    @Query("SELECT producer " +
            "FROM ProducerEntity producer " +
            "WHERE producer.accreditation <> com.antikryptonite.outsourcing.entities.AccreditationType.BLOCK AND producer.request = false AND producer.confirm = true")
    List<ProducerEntity> getSilentProducers();

    /**
     * Поиск поставщика по id пользователя
     */
    Optional<ProducerEntity> findByUserId(UUID userId);

    /**
     * Возвращает вообще всех поставщиков, кроме ЧС
     */
    @Query("SELECT producer " +
            "FROM ProducerEntity producer " +
            "WHERE producer.accreditation <> com.antikryptonite.outsourcing.entities.AccreditationType.BLOCK")
    List<ProducerEntity> getOverallProducers();

    /**
     * Поиск поставщика по токену Telegram
     */
    Optional<ProducerEntity> findByTelegramToken(UUID token);

    /**
     * Существует ли поставщик с данным id пользователя
     */
    boolean existsByUserId(UUID userId);

}
