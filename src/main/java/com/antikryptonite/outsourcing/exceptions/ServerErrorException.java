package com.antikryptonite.outsourcing.exceptions;

public class ServerErrorException extends ApplicationException {

    /**
     * @param message тип или название ненайденного объекта
     */
    public ServerErrorException(String message) { super(message); }

}
