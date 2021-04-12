package com.antikryptonite.outsourcing.dto.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Тело ответа на запрос о получении списка сотрудников
 */
@Data
public class WorkersResponse implements Serializable {

    private List<WorkerResponse> workers;

}
