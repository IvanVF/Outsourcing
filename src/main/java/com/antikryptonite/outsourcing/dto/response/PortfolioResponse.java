package com.antikryptonite.outsourcing.dto.response;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * Тело ответа на запрос о портфолио
 */
@Data
public class PortfolioResponse {

    @Length(max = 200)
    private String customer;

    @Length(max = 15000)
    private String description;

    @Length(max = 15000)
    private String url;
}
