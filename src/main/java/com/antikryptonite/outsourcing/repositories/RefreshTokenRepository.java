package com.antikryptonite.outsourcing.repositories;

import com.antikryptonite.outsourcing.entities.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Репозиторий для сессий рефреш-токенов
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID> {

    /**
     * Находит сессию по токену
     * @param refreshToken токен
     * @return RefreshTokenRepository
     */
    Optional<RefreshTokenEntity> findByRefreshToken(UUID refreshToken);

    /**
     * Удаление рефреш-токенов по id пользователя
     * @param userId - id пользователя
     */
    void deleteAllByUserId(UUID userId);
}
