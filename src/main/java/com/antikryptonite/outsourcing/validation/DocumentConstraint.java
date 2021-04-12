package com.antikryptonite.outsourcing.validation;

import javax.validation.*;
import java.lang.annotation.*;

/**
 * Аннотация валидатора для валдиации файла на его тип
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DocumentValidator.class)
@Documented
public @interface DocumentConstraint {

    /**
     * Сообщение ошибки валидации
     */
    String message() default "Content type is wrong";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
