package com.antikryptonite.outsourcing.services;

import com.antikryptonite.outsourcing.configurations.security.jwt.JwtProvider;
import com.antikryptonite.outsourcing.dto.request.*;
import com.antikryptonite.outsourcing.dto.response.AuthResponse;
import com.antikryptonite.outsourcing.entities.*;
import com.antikryptonite.outsourcing.exceptions.*;
import com.antikryptonite.outsourcing.repositories.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.*;
import java.util.UUID;

/**
 * Сервис аутентификации
 */
@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    private final ProducerRepository producerRepository;

    private JwtProvider jwtProvider;

    private PasswordEncoder passwordEncoder;

    private long refreshTimeMillis;

    /**
     * Конструктор сервиса
     */
    @Autowired
    public AuthService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository, ProducerRepository producerRepository) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.producerRepository = producerRepository;
    }

    /**
     * Получение токена по логину
     * @param request - параметры пользователя
     * @return - возвращает найденный токен по логину
     */
    public AuthResponse giveTokensByPassword(AuthRequest request) throws ApplicationException {
        UserEntity user = userRepository.findByLogin(request.getLogin()).orElseThrow(AuthWrongException::new);
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthWrongException();
        }
        if (user.getRole() == Role.BAD) {
            throw new AuthWrongException();
        }
        if (producerRepository.existsByUserId(user.getId())) {
            ProducerEntity producer = producerRepository.findByUserId(user.getId()).orElseThrow(RegistrationFallsException::new);
            if (!producer.isConfirm()) {
                throw new NonConfirmRegistrationException();
            }
        }
        return generateTokens(user, request.getFingerprint());
    }

    /**
     * Получение токенов по refresh токенам
     * @param refreshRequest рефреш токен и fingerprint
     * @return токены
     */
    public AuthResponse giveTokensByRefreshToken(RefreshRequest refreshRequest) throws AuthWrongException {
        try {
            UUID refreshToken = UUID.fromString(refreshRequest.getRefreshToken());
            RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.findByRefreshToken(refreshToken).orElseThrow(AuthWrongException::new);

            if (refreshTokenEntity.getUser().getRole() == Role.BAD) {
                throw new AuthWrongException();
            }

            if (!refreshTokenEntity.getFingerprint().equals(refreshRequest.getFingerprint())) {
                throw new AuthWrongException();
            }

            LocalDateTime now = LocalDateTime.now();
            if (now.isAfter(refreshTokenEntity.getExpTime())) {
                throw new AuthWrongException();
            }

            refreshTokenEntity.getUser().getRefreshTokens().remove(refreshTokenEntity);
            refreshTokenRepository.delete(refreshTokenEntity);
            return generateTokens(refreshTokenEntity.getUser(), refreshRequest.getFingerprint());
        } catch (IllegalArgumentException ex) {
            throw new AuthWrongException();
        }
    }

    private AuthResponse generateTokens(UserEntity userEntity, String fingerprint) {
        String accessToken = jwtProvider.generateToken(userEntity.getId(), userEntity.getRole());
        LocalDateTime now = LocalDateTime.now();

        RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity();
        refreshTokenEntity.setId(UUID.randomUUID());
        refreshTokenEntity.setFingerprint(fingerprint);

        UUID refreshToken = UUID.randomUUID();
        refreshTokenEntity.setRefreshToken(refreshToken);
        refreshTokenEntity.setExpTime(now.plus(refreshTimeMillis, ChronoUnit.MILLIS));
        refreshTokenEntity.setUser(userEntity);
        refreshTokenRepository.save(refreshTokenEntity);
        userEntity.getRefreshTokens().add(refreshTokenEntity);

        return new AuthResponse(accessToken, refreshToken.toString());
    }

    /**
     * @param passwordEncoder passwordEncoder
     */
    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * @param jwtProvider jwtProvider
     */
    @Autowired
    public void setJwtProvider(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    /**
     * Срок жизни рефреш-токена
     *
     * @param refreshTimeMillis в миллисекундах
     */
    @Value("${refresh.token.time}")
    public void setRefreshTimeMillis(long refreshTimeMillis) {
        this.refreshTimeMillis = refreshTimeMillis;
    }
}