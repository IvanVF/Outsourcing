package com.antikryptonite.outsourcing.dto.request;

import com.antikryptonite.outsourcing.entities.StatusPurchase;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.util.*;

/**
 * Запрос для завершения закупки
 */
@Data
public class PurchaseCloseRequest {

    @NotNull
    private StatusPurchase status;

    private List<UUID> winners;

    @Size(min = 2, max = 10000)
    private String closingDescription;

    @Size(min = 1, max = 10)
    private List<UUID> finishDocuments;

}
