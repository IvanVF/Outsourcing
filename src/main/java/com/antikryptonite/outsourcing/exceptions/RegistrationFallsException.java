package com.antikryptonite.outsourcing.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Исключение, когда код подтверждения регистрации не прошёл проверку
 */
@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class RegistrationFallsException extends ApplicationException {

}
