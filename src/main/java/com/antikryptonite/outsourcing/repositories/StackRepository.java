package com.antikryptonite.outsourcing.repositories;

import com.antikryptonite.outsourcing.entities.StackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Репозиторий стека применяемых технологий
 */
@Repository
public interface StackRepository extends JpaRepository<StackEntity, UUID> {
}
