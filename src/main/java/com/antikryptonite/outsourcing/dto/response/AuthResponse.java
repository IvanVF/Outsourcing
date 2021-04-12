package com.antikryptonite.outsourcing.dto.response;

import lombok.*;

/**
 * Тело ответа аутентификации
 */
@Data
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;

    private String refreshToken;
}
