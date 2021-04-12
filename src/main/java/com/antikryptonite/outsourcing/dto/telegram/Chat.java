package com.antikryptonite.outsourcing.dto.telegram;

import lombok.Data;

/**
 * Объект Chat для Telegram Bot API
 */
@Data
public class Chat {

    private Integer id;

    private ChatType type;
}
