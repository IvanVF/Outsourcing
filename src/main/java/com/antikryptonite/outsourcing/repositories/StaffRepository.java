package com.antikryptonite.outsourcing.repositories;

import com.antikryptonite.outsourcing.entities.StaffEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Репозиторий команды поставщиков
 */
@Repository
public interface StaffRepository extends JpaRepository<StaffEntity, UUID> {
}
