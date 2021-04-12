package com.antikryptonite.outsourcing.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Исключение на уникальность email
 */
@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class UniqueException extends ApplicationException {

    /**
     * @param message - сообщение исключения
     */
    public UniqueException(String message) {
        super(message);
    }
}