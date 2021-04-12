package com.antikryptonite.outsourcing.dto.request;

import com.antikryptonite.outsourcing.validation.*;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;

/**
 * Тело запроса на регистрацию физического лица
 */
@Data
public class RegistrationIndividualRequest {

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
    @INN
    @Length(min = 12, max = 12)
    private String innNumber;

    @NotNull
    @Max(9999999999L)
    @Min(1000000000L)
    private Long phoneNumber;

}