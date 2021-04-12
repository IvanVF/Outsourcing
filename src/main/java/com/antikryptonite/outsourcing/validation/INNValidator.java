package com.antikryptonite.outsourcing.validation;

import javax.validation.*;

/**
 * Валидатор ИНН
 */
public class INNValidator implements ConstraintValidator<INN, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        return value.matches("[\\d+$]+");

    }
}
