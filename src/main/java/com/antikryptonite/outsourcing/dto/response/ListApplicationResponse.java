package com.antikryptonite.outsourcing.dto.response;

import com.antikryptonite.outsourcing.serializers.LocalDateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.time.LocalDate;
import java.util.*;

/**
 * Ответ по заявке в списке заявок
 */
@Data
public class ListApplicationResponse {

    private UUID id;

    private String description;

    private Long price;

    private UUID producerId;

    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate publicationDate;

    private List<UUID> documents;

    private boolean individual;

    private String inn;

    private String firstName;

    private String middleName;

    private String lastName;

    private String orgName;
}
