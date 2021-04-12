package com.antikryptonite.outsourcing.configurations.security.jwt;

import com.antikryptonite.outsourcing.entities.Role;
import io.jsonwebtoken.*;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Компонент для генерации и проверки JWT
 */
@Component
@Log
public class JwtProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.token.time}")
    private long validityInMilliseconds;

    private static final String ROLE_CLAIM = "role";

    /**
     * Генерация токена
     *
     * @param userId - id пользователя
     * @return - возвращает сгенерированный токен
     */
    public String generateToken(UUID userId, Role role) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);
        Map<String, Object> map = new HashMap<>();{
            map.put(ROLE_CLAIM, role.name());
        }
        return Jwts.builder()
                .setSubject(userId.toString())
                .setExpiration(validity)
                .addClaims(map)
                //.addClaims(Map.of(ROLE_CLAIM, role.name()))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    /**
     * Проверка на валидность токена
     *
     * @param token - проверяемый токен
     * @return - возвращает true или false, в зависимости от валидности токена
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.severe("Invalid token");
        }
        return false;
    }

    /**
     * Получение информации из токена
     *
     * @param token access токен
     * @return информация из токена
     */
    public TokenInformation getTokenInformation(String token) {
        Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
        return new TokenInformation(claims.getSubject(), Role.valueOf(claims.get(ROLE_CLAIM).toString()));
    }
}