package com.antikryptonite.outsourcing.validation;

import java.lang.annotation.Documented;
import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FIOConstraintValidator.class)
@Target({ ElementType.FIELD, ElementType.LOCAL_VARIABLE })
@Retention(RetentionPolicy.RUNTIME)
public @interface FIO {

    /**
     * Сообщение ошибки валидации
     */
    String message() default "validation fails";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
