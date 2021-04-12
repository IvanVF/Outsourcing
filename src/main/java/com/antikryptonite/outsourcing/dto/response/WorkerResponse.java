package com.antikryptonite.outsourcing.dto.response;

import com.antikryptonite.outsourcing.entities.*;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

/**
 * Тело ответа на запрос о получении информации сотрудника
 */
@Data
public class WorkerResponse implements Serializable {

    private UUID id;

    private String login;

    private String firstName;

    private String lastName;

    private String middleName;

    private String role;
}
