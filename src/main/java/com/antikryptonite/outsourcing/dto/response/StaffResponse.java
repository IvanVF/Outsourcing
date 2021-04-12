package com.antikryptonite.outsourcing.dto.response;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;

/**
 * Тело ответа на запрос о состава команды разработчиков поставщика
 */
@Data
public class StaffResponse {

    @Length(max = 100)
    @NotEmpty
    private String activity;

    @Min(0)
    private int headcount;

}
