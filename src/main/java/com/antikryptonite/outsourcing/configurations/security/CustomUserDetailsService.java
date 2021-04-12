package com.antikryptonite.outsourcing.configurations.security;

import com.antikryptonite.outsourcing.entities.UserEntity;
import com.antikryptonite.outsourcing.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Сервис для получения учетных данных пользователя
 */
@Component
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Конструктор сервиса для получения учетных данных пользователя
     */
    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public CustomUserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return CustomUserDetails.fromUserEntityToCustomUserDetails(userEntity);
    }
}
