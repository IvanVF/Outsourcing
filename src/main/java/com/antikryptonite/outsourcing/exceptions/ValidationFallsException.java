package com.antikryptonite.outsourcing.exceptions;

/**
 * Общее исключение 400
 */
public class ValidationFallsException extends ApplicationException {

    /**
     * Конструктор
     */
    public ValidationFallsException(String message) {
        super(message);
    }

    /**
     * Конструктор
     */
    public ValidationFallsException(String message, Throwable cause) {
        super(message, cause);
    }
}
