package com.antikryptonite.outsourcing.entities;

import com.antikryptonite.outsourcing.dto.response.AuditResponse;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Тело ответа на запрос о получении списка действий из таблицы аудита
 */
@Data
public class AuditFullResponse implements Serializable {

    private List<AuditResponse> actions;

    private long totalLength;

}
