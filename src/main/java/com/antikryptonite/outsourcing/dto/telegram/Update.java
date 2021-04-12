package com.antikryptonite.outsourcing.dto.telegram;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

/**
 * Объект Update из Telegram Bot API
 */
@Data
public class Update {
    @JsonProperty("update_id")
    private Integer updateId;

    private Message message;
}
