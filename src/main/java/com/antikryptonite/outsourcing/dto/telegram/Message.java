package com.antikryptonite.outsourcing.dto.telegram;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Объект Message из Telegram Bot API
 */
@Data
public class Message {
    @JsonProperty("message_id")
    private Integer messageId;

    private User from;

    private Chat chat;

    private String text;
}
