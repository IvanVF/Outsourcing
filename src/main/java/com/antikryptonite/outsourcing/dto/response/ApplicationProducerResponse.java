package com.antikryptonite.outsourcing.dto.response;

import com.antikryptonite.outsourcing.serializers.LocalDateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Ответ о списке заявок конкретного поставщика
 */
@Data
public class ApplicationProducerResponse {

    private UUID purchaseId;

    private String purchaseName;

    private UUID applicationId;

    private String description;

    private Long price;

    private UUID producerId;

    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate publicationDate;

    private List<UUID> documents;

}
