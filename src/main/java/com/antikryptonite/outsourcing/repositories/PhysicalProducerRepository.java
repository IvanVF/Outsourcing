package com.antikryptonite.outsourcing.repositories;

import com.antikryptonite.outsourcing.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Репозиторий физических лиц
 */
@Repository
public interface PhysicalProducerRepository extends JpaRepository<IndividualProducerEntity, UUID> {

    /**
     * Найти физ. лицо по поставщику
     */
    Optional<IndividualProducerEntity> getByProducer(ProducerEntity producer);
}
