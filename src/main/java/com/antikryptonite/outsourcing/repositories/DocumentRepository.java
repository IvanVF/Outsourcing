package com.antikryptonite.outsourcing.repositories;

import com.antikryptonite.outsourcing.entities.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Репозиторий документов
 */
@Repository
public interface DocumentRepository extends JpaRepository<DocumentEntity, UUID> {
}
