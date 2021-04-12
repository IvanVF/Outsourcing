package com.antikryptonite.outsourcing.dto.request;

import lombok.Data;
import org.hibernate.validator.constraints.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.*;

/**
 * Запрос на создание заявки
 */
@Data
public class ApplicationRequest {

    @Length(min = 2, max = 10000)
    @NotBlank
    private String description;

    @Range(min = 0)
    private Long price;

    @Size(min = 1, max = 10)
    private Set<UUID> documents;
}
