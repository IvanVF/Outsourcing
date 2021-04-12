package com.antikryptonite.outsourcing.dto.response;

import com.antikryptonite.outsourcing.entities.Role;
import com.antikryptonite.outsourcing.serializers.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Тело ответа на запрос о одном поле таблицы аудита
 */
@Data
public class AuditResponse {

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime actionTime;

    private String firstName;

    private String middleName;

    private String lastName;

    private Role role;

    private String action;

}
