package com.antikryptonite.outsourcing.services;

import com.antikryptonite.outsourcing.dto.telegram.*;
import com.antikryptonite.outsourcing.entities.*;
import com.antikryptonite.outsourcing.exceptions.*;
import com.antikryptonite.outsourcing.repositories.*;
import com.antikryptonite.outsourcing.telegram.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Сервис для операций с данными Telegram
 */
@Service
@Transactional
public class TelegramService implements InitializingBean {

    public static final String TELEGRAM_URL = "https://t.me/";

    private final ProducerRepository producerRepository;


    private final TelegramRepository telegramRepository;

    private TelegramSender telegramSender;

    private boolean doSetWebhook;

    private String botName;

    /**
     * Конструктор
     */
    public TelegramService(ProducerRepository producerRepository, TelegramRepository telegramRepository) {
        this.producerRepository = producerRepository;
        this.telegramRepository = telegramRepository;
    }

    /**
     * Добавить поставщика по телефону в рассылку
     */
    public void catchContact(Update update) {
        Message message = update.getMessage();
        if (message == null) {
            return;
        }

        String text = message.getText();
        if (text == null || text.isEmpty()) {
            return;
        }

        String[] words = text.trim().split(" ");
        if (words.length == 0) {
            return;
        }

        if (!"/start".equals(words[0])) {
            return;
        }

        if (words.length == 1) {
            telegramSender.sendMessage(new TelegramMessage(
                    message.getChat().getId(),
                    "Здравствуйте. Чтобы подключиться к рассылке сообщений, вы должны зарегистрироваться в системе."
            ));
            return;
        }

        if (words.length != 2) {
            telegramSender.sendMessage(new TelegramMessage(message.getChat().getId(), "Неверный формат команды."));
        }

        UUID token;
        try {
            token = UUID.fromString(words[1]);
        } catch (IllegalArgumentException ex) {
            telegramSender.sendMessage(new TelegramMessage(message.getChat().getId(), "Неверный токен."));
            return;
        }

        Optional<ProducerEntity> producerOptional = producerRepository.findByTelegramToken(token);
        if (!producerOptional.isPresent()) {
            telegramSender.sendMessage(new TelegramMessage(message.getChat().getId(), "Неверный токен."));
            return;
        }
        ProducerEntity producer = producerOptional.get();
        if (telegramRepository.existsByTelegramIdAndProducer(message.getChat().getId(), producer)) {
            telegramSender.sendMessage(new TelegramMessage(message.getChat().getId(), "Вы уже зарегистрированы в нашей рассылке!"));
            return;
        }
        TelegramEntity telegramEntity = new TelegramEntity();
        telegramEntity.setId(UUID.randomUUID());
        telegramEntity.setTelegramId(message.getChat().getId());
        telegramEntity.setProducer(producer);
        telegramRepository.save(telegramEntity);

        telegramSender.sendMessage(new TelegramMessage(telegramEntity.getTelegramId(), "Вы успешно зарегистрировались в рассылке!"));
    }

    /**
     * Возвращает URL бота с токеном поставщика
     */
    public String getURL() throws ApplicationException {
        UUID userId = SecurityUtil.getUserId();
        ProducerEntity producerEntity =
                producerRepository.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException(String.format("User %s", userId)));
        return TELEGRAM_URL + botName + "?start=" + producerEntity.getTelegramToken();
    }

    private void setWebhook() {
        telegramSender.setWebhook();
    }

    /**
     * Нужно ли при инициализации ставить вебхук
     */
    @Value("${telegram.webhook}")
    public void setDoSetWebhook(boolean doSetWebhook) {
        this.doSetWebhook = doSetWebhook;
    }

    /**
     * telegramSender сеттер
     */
    @Autowired
    public void setTelegramSender(TelegramSender telegramSender) {
        this.telegramSender = telegramSender;
    }

    /**
     * Сеттер имени бота
     */
    @Value("${telegram.bot.name}")
    public void setBotName(String botName) {
        this.botName = botName;
    }

    @Override
    public void afterPropertiesSet() {
        if (doSetWebhook) {
            setWebhook();
        }
    }
}
