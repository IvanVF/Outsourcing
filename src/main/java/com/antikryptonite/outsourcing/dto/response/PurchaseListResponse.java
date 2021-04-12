package com.antikryptonite.outsourcing.dto.response;

import com.antikryptonite.outsourcing.entities.*;
import com.antikryptonite.outsourcing.serializers.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.time.LocalDate;
import java.util.*;

/**
 * Ответ тела закупки при запросе списка закупок
 */
@Data
public class PurchaseListResponse {

    private UUID purchaseId;

    private String name;

    private Integer number;

    private String description;

    private Long startingPrice;

    private CurrencyValue currency;

    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate startDate;

    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate finishDate;

    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate publicationDate;

    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate closingDate;

    private List<String> stack;

    private boolean additionalStagePossible;

    private String status;

}