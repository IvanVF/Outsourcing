package com.antikryptonite.outsourcing.dto.request;

import com.antikryptonite.outsourcing.validation.Password;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Тело запроса на смену пароля администратором
 */
@Data
public class ChangePasswordAdminRequest {

    @NotBlank
    @Password
    private String passwordNew;

}
