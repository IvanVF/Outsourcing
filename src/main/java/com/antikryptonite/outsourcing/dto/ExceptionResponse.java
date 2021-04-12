package com.antikryptonite.outsourcing.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Ответ на ошибки-исключения
 */
@Data
public class ExceptionResponse implements Serializable {

    private int httpCode;

    private String message;

    private List<String> descriptionList;
}
