package com.antikryptonite.outsourcing.dto.request;

import com.antikryptonite.outsourcing.entities.CurrencyValue;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Запрос для создания закупки
 */
@Data
public class PurchaseRequest {

    @NotBlank
    @Length(min = 2, max = 320)
    private String name;

    @Length(max = 10000)
    private String description;

    @NotBlank
    @Length(min = 2, max = 320)
    private String subject;

    @Min(0)
    private Long startingPrice;

    @NotNull
    private CurrencyValue currency;

    @NotNull
    @FutureOrPresent
    private LocalDate startDate;

    @NotNull
    @Future
    private LocalDate finishDate;

    private List<String> stack;

    @NotNull
    private boolean additionalStagePossible;

    private UUID parent;

    @Size(max = 10)
    private List<UUID> startDocuments;

}
