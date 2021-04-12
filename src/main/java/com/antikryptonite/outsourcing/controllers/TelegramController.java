package com.antikryptonite.outsourcing.controllers;

import com.antikryptonite.outsourcing.dto.telegram.Update;
import com.antikryptonite.outsourcing.exceptions.ApplicationException;
import com.antikryptonite.outsourcing.services.TelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для вебхука с Telegram
 */
@RestController
public class TelegramController {
    private final TelegramService telegramService;

    /**
     * Конструктор
     */
    @Autowired
    public TelegramController(TelegramService telegramService) {
        this.telegramService = telegramService;
    }

    /**
     * Получить сообщение от Telegram бота
     */
    @PostMapping("/telegram/webhook")
    public void catchContactInfo(@RequestBody Update update) {
        telegramService.catchContact(update);
    }

    /**
     * Получить ссылку на бота с токеном поставщика
     * @return полный URL-адрес на бота
     */
    @PreAuthorize("hasAnyRole('ROLE_PRODUCER', 'ROLE_USER')")
    @GetMapping(value = "/telegram/url", produces = {"text/plain"})
    public String getTelegramToken() throws ApplicationException {
        return telegramService.getURL();
    }
}
