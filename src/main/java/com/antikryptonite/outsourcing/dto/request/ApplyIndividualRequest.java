package com.antikryptonite.outsourcing.dto.request;

import com.antikryptonite.outsourcing.dto.response.PortfolioResponse;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.util.List;
import java.util.UUID;

/**
 * Запрос для заявки на аккредитацию физ лица
 */
@Data
public class ApplyIndividualRequest {

    @Min(1000000000L) @Max(9999999999L)
    private Long phone;

    @NotNull
    @Size(min = 1)
    private List<@Size(min = 1, max = 100) String> stack;

    @NotBlank
    private String specialization;

    private List<PortfolioResponse> portfolio;

    @Size(max = 10)
    private List<UUID> documents;

    @Length(max = 10000)
    private String url;

}
