package com.antikryptonite.outsourcing.exceptions;

import javassist.NotFoundException;

public class ResourceNotFoundException extends ApplicationException {

    /**
     * @param message причина ошибки на сервере
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

}
