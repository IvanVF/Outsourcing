package com.antikryptonite.outsourcing.repositories;

import com.antikryptonite.outsourcing.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Репозиторий для юридических лиц
 */
@Repository
public interface EntityProducerRepository extends JpaRepository<EntityProducerEntity, UUID> {
    /**
     * Найти юр. лицо по поставщику
     */
    Optional<EntityProducerEntity> getByProducer(ProducerEntity producer);
}
