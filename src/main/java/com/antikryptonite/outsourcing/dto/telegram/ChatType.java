package com.antikryptonite.outsourcing.dto.telegram;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Типы Telegram-чатов
 */
public enum ChatType {
    @JsonProperty("private")
    PRIVATE,
    @JsonProperty("group")
    GROUP,
    @JsonProperty("supergroup")
    SUPERGROUP,
    @JsonProperty("channel")
    CHANNEL
}
