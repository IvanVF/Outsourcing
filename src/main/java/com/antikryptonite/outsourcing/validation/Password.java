package com.antikryptonite.outsourcing.validation;

import javax.validation.*;
import java.lang.annotation.*;

/**
 * Аннотация валидатора для валдиации пароля
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValidator.class)
@Documented
public @interface Password {

    /**
     * Сообщение ошибки валидации
     */
    String message() default "validation fails";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
