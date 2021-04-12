package com.antikryptonite.outsourcing.dto.response;

import com.antikryptonite.outsourcing.entities.*;
import com.antikryptonite.outsourcing.serializers.LocalDateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Ответ о созданной закупке
 */
@Data
public class PurchaseCreateResponse {

    private UUID purchaseId;

    private String name;

    private Integer number;

    private String description;

    private String subject;

    private Long startingPrice;

    private CurrencyValue currency;

    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate startDate;

    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate finishDate;

    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate publicationDate;

    private List<String> stack;

    private boolean additionalStagePossible;

    private StatusPurchase status;

    private PurchaseRelativeFieldResponse relative;

    private List<UUID> startDocuments;

}
