package com.antikryptonite.outsourcing.dto.request;

import com.antikryptonite.outsourcing.validation.Password;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Тело запроса на смену пароля пользователем
 */
@Data
public class ChangePasswordRequest {

    @NotBlank
    @Password
    private String passwordOld;

    @NotBlank
    @Password
    private String passwordNew;

}
