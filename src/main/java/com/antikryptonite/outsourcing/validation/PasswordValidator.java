package com.antikryptonite.outsourcing.validation;

import lombok.extern.java.Log;

import javax.validation.*;

/**
 * Валидатор для проверки файла на его тип
 */
@Log
public class PasswordValidator implements ConstraintValidator<Password, String> {


    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        if (value.isEmpty()) {
            return false;
        }
        if (value.length() < 6 || value.length() > 100) {
            return false;
        }


        if (value.matches("[^a-zA-Z0-9#?!@$%^&*-]")) {
            return false;
        }
        if (!value.matches(".*\\d.*")) {
            return false;
        }
        return value.matches(".*[A-Z].*");
    }
}
