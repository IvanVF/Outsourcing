package com.antikryptonite.outsourcing.dto.response;

import lombok.Data;

import java.util.UUID;

/**
 * Ответ-информация о "родственнике" этапа
 */
@Data
public class PurchaseRelativeFieldResponse {

    private UUID id;

    private boolean parent;

}
