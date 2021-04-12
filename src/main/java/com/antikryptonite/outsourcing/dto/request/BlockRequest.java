package com.antikryptonite.outsourcing.dto.request;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * Комментарий при добавлении поставщика в ЧС
 */
@Data
public class BlockRequest {

    @NotBlank
    @Length(min = 1, max = 100000)
    String blockComment;

}
