package com.antikryptonite.outsourcing.dto.telegram;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Объект User из Telegram Bot API
 */
@Data
public class User {
    private Integer id;

    @JsonProperty("is_bot")
    private boolean bot;
}
