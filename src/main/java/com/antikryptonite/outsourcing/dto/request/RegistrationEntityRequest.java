package com.antikryptonite.outsourcing.dto.request;

import com.antikryptonite.outsourcing.validation.*;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;

/**
 * Тело запроса на регистрацию юридического лица
 */
@Data
public class RegistrationEntityRequest {

    @Email
    @NotBlank
    @Length(min = 6, max = 320)
    private String login;

    @NotBlank
    @Password
    private String password;

    @NotBlank
    @Length(min = 1, max = 200)
    private String orgName;

    @NotNull
    @INN
    @Length(min = 10, max = 10)
    private String innNumber;

    @NotNull
    @Max(9999999999L)
    @Min(1000000000L)
    private Long phoneNumber;

}