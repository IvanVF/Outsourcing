package com.antikryptonite.outsourcing.dto.response;

import com.antikryptonite.outsourcing.entities.*;
import com.antikryptonite.outsourcing.serializers.LocalDateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.time.LocalDate;
import java.util.*;

/**
 * Ответ с полной информацией о закупке
 */
@Data
public class PurchaseResponse {

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

    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate closingDate;

    private List<String> stack;

    private boolean additionalStagePossible;

    private String status;

    private PurchaseRelativeFieldResponse relative;

    //private List<UUID> winners;
    private List<BriefProducerInfo> winners;
    
    private String closingDescription;

    private List<UUID> startDocuments;

    private List<UUID> finishDocuments;

}
