package com.antikryptonite.outsourcing.dto.telegram;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Объект Contact из Telegram Bot API
 */
@Data
public class Contact {
    @JsonProperty("phone_number")
    private String phone;

    @JsonProperty("user_id")
    private Integer userId;
}
