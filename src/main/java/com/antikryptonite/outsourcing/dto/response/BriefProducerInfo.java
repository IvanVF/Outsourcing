package com.antikryptonite.outsourcing.dto.response;

import lombok.*;

/**
 * Краткая информация о поставщике
 */
@Data
public class BriefProducerInfo {
    private boolean individual;

    private String orgName;

    private String firstName;

    private String lastName;

    private String middleName;
}
