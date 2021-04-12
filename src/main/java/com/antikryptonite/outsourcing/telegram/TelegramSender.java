package com.antikryptonite.outsourcing.telegram;

import com.antikryptonite.outsourcing.entities.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.logging.Level;

/**
 * Компонент для связи с Telegram-ботом
 */
@Component
@Log
public class TelegramSender {

    private static final String TELEGRAM_URL = "https://api.telegram.org/bot";

    private final WebClient webClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String url;

    /**
     * Конструктор
     */
    public TelegramSender(@Value("${telegram.bot.token}") String botToken) {

        webClient = WebClient.create(TELEGRAM_URL + botToken + "/");
    }

    /**
     * Отправить сообщение в телеграм
     */
    public void sendMessage(TelegramMessage message) {

        Map<String, String> sentMessage = new HashMap<>();
        sentMessage.put("chat_id", String.valueOf(message.getUserId()));
        sentMessage.put("text", message.getText());

        try {
            webClient
                    .post()
                    .uri("sendMessage")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(objectMapper.writeValueAsString(sentMessage))
                    .retrieve().bodyToMono(String.class).block();
        } catch (Exception ex) {
            log.log(Level.WARNING, "Error: cannot send message");
        }
    }

    /**
     * Отправить сообщение данному поставщику
     */
    public void sendMessage(ProducerEntity producerEntity, String message) {
        if (producerEntity.getTelegramIds() == null || producerEntity.getTelegramIds().isEmpty()) {
            return;
        }

        producerEntity.getTelegramIds().forEach(telegramEntity -> sendMessage(new TelegramMessage(telegramEntity.getTelegramId(), message)));
    }

    /**
     * Установить в боте вебхук на url
     */
    public void setWebhook() {
        Map<String, Object> sentMessage = new HashMap<>();
        sentMessage.put("url", url + "/telegram/webhook");
        sentMessage.put("allowed_updated", Collections.singletonList("message"));
        try {
            webClient.post()
                    .uri("setWebhook")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(objectMapper.writeValueAsString(sentMessage))
                    .retrieve().bodyToMono(String.class).block();
        } catch (Exception ex) {
            log.log(Level.WARNING, "Error: cannot set webhook");
        }
    }

    /**
     * Устанавливает url, на который будет ставиться webhook
     */
    @Value("${application.url}")
    public void setUrl(String url) {
        this.url = url;
    }
}
