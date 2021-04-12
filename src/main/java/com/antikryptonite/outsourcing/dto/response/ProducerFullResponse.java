package com.antikryptonite.outsourcing.dto.response;

import com.antikryptonite.outsourcing.serializers.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.time.*;
import java.util.*;

/**
 * Полное описание поставщика
 */
@Data
public class ProducerFullResponse {

    private UUID id;

    private boolean individual;

    private String firstName;

    private String middleName;

    private String lastName;

    private String orgName;

    private String inn;

    private String accreditation;

    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate registrationDate;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime accreditationTime;

    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate blockDate;

    private String blockComment;

    private List<Long> phones;

    private List<String> stacks;

    private boolean request;

    private ContactPersonResponse contactPerson;

    private String url;

    private String legalAddress;

    private String actualAddress;

    private Integer headcount;

    private List<StaffResponse> staff;

    private String specialization;

    private List<PortfolioResponse> portfolio;

    private String agencies;

    private List<UUID> documents;
}
