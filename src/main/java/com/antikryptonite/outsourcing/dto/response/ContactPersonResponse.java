package com.antikryptonite.outsourcing.dto.response;

import com.antikryptonite.outsourcing.validation.FIO;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * Тело ответа на запрос о контактном лице поставщика
 */
@Data
public class ContactPersonResponse {

    @Length(min = 2, max = 40)
    @FIO
    private String firstName;

    @Length(min = 1, max = 40)
    @FIO
    private String middleName;

    @Length(min = 2, max = 40)
    @FIO
    private String lastName;

    @Length(max = 100)
    private String position;
}
