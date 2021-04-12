package com.antikryptonite.outsourcing.configurations.security.jwt;

import com.antikryptonite.outsourcing.entities.Role;
import lombok.*;

/**
 * Информация, заносимая в access token
 */
@Data
@AllArgsConstructor
public class TokenInformation {
    private String userId;

    private Role role;
}
