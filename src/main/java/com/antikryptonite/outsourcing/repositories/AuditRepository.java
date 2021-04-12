package com.antikryptonite.outsourcing.repositories;

import com.antikryptonite.outsourcing.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Репозиторий для истории действий
 */
@Repository
public interface AuditRepository extends JpaRepository<AuditEntity, UUID> {

    /**
     * Считает количество всех записей по роли
     */
    long countByRole(Role role);
}
