package com.antikryptonite.outsourcing.repositories;

import com.antikryptonite.outsourcing.entities.PortfolioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Репозиторий портфолио поставщиков
 */
@Repository
public interface PortfolioRepository extends JpaRepository<PortfolioEntity, UUID> {
}
