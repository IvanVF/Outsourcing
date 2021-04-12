package com.antikryptonite.outsourcing.dto.request;

import com.antikryptonite.outsourcing.entities.Role;
import com.antikryptonite.outsourcing.validation.*;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;

/**
 * Тело запроса на регистрацию сотрудника
 */
@Data
public class RegistrationWorkerRequest {

    @Email
    @NotBlank
    @Length(min = 6, max = 320)
    private String login;

    @NotBlank
    @Password
    private String password;

    @NotBlank
    @FIO
    @Length(min = 2, max = 40)
    private String firstName;

    @NotBlank
    @FIO
    @Length(min = 2, max = 40)
    private String lastName;

    @NotBlank
    @FIO
    @Length(min = 1, max = 40)
    private String middleName;

    @NotNull
    private Role type;

}