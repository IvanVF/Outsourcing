package com.antikryptonite.outsourcing.repositories;

import com.antikryptonite.outsourcing.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Репозиторий пользователя
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    /**
     * Поиск пользователя по логину
     * @param login - логин пользователя
     */
    Optional<UserEntity> findByLogin(String login);

    /**
     * Поиск пользователя по роли
     * @param role - роль пользователя
     */
    Optional<UserEntity> findByRole(Role role);
}