package com.antikryptonite.outsourcing.dto.response;

import com.antikryptonite.outsourcing.serializers.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.io.Serializable;
import java.time.*;
import java.util.*;

/**
 * Тело ответа на запрос о получении информации о поставщике
 */
@Data
public class ProducerResponse implements Serializable {

    private UUID producerId;

    private boolean individual;

    private String firstName;

    private String middleName;

    private String lastName;

    private String organizationName;

    private String accreditation;

    private String inn;

    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate registrationDate;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime accreditationTime;

    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate blockDate;

    private String blockComment;

    private boolean request;

    private String specialization;

    private Integer headcount;

    private List<String> stack;

}
