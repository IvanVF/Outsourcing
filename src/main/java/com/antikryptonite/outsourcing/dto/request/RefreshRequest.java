package com.antikryptonite.outsourcing.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Запрос для обновления токенов
 */
@Data
public class RefreshRequest {
    @NotBlank
    private String refreshToken;

    @NotBlank
    private String fingerprint;
}
