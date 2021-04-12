package com.antikryptonite.outsourcing.exceptions;

/**
 * Исключение для ошибок загриузок файлов
 */
public class FileUploadException extends ApplicationException {

    /**
     * Констуктор
     */
    public FileUploadException(String message) {
        super(message);
    }

    /**
     * Конструктор
     */
    public FileUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
