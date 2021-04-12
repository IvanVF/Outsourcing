package com.antikryptonite.outsourcing.telegram;

import lombok.*;

/**
 * Тело сообщения для telegram-бота
 */
@Data
@AllArgsConstructor
public class TelegramMessage {
    private int userId;

    private String text;
}
