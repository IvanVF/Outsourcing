package com.antikryptonite.outsourcing.dto.request;

import com.antikryptonite.outsourcing.dto.response.ContactPersonResponse;
import com.antikryptonite.outsourcing.dto.response.PortfolioResponse;
import com.antikryptonite.outsourcing.dto.response.StaffResponse;
import lombok.Data;
import org.hibernate.validator.constraints.*;

import javax.validation.constraints.*;
import java.util.*;

/**
 * Запрос для заявки на аккредитацию юр лица
 */
@Data
public class ApplyEntityRequest {

    @Min(1000000000L) @Max(9999999999L)
    private Long phone;

    @NotNull
    @Size(min = 1)
    private List<@Size(min = 1, max = 100) String> stack;

    private ContactPersonResponse contactPerson;

    @Length(max = 10000)
    private String url;

    //@NotBlank
    @Length(max = 200)
    private String legalAddress;

    //@NotBlank
    @Length(max = 200)
    private String actualAddress;

    //@Min(1)
    private Integer headcount;

    //@NotNull
    private List<StaffResponse> staff = new ArrayList<>();

    //@NotBlank
    private String specialization;

    private List<PortfolioResponse> portfolio = new ArrayList<>();

    @Length(max = 10000)
    private String agencies;

    @Size(max = 10)
    private List<UUID> documents;

}
