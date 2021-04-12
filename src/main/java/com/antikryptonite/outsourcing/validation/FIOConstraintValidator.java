package com.antikryptonite.outsourcing.validation;
import lombok.extern.java.Log;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Валидатор имени/фамилии/отчества
 */
@Log
public class FIOConstraintValidator implements ConstraintValidator<FIO, String> {

    @Override
    public boolean isValid(String fio, ConstraintValidatorContext cxt) {
        if (fio == null) {
            return false;
        }
        return fio.matches("[a-zA-Z-]+") || fio.matches("[а-яА-яёЁ-]+");

    }
}
