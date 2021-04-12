package com.antikryptonite.outsourcing.dto.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Тело ответа на запрос о получении списка поставщиков
 */
@Data
public class ProducersResponse implements Serializable {

    private List<ProducerResponse> producersList;

}
