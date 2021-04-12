package com.antikryptonite.outsourcing.validation;

import javax.validation.*;
import java.lang.annotation.*;

/**
 * Аннотация валидатора ИНН
 */
@Documented
@Constraint(validatedBy = INNValidator.class)
@Target({ ElementType.FIELD, ElementType.LOCAL_VARIABLE })
@Retention(RetentionPolicy.RUNTIME)
public @interface INN {

    /**
     * Сообщение ошибки валидации
     */
    String message() default "validation fails";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
