package com.antikryptonite.outsourcing.mail;

import org.springframework.beans.factory.annotation.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Компонент для отправки сообщений
 */
@Component
public class EmailSender {
    private final String emailFrom;

    private JavaMailSender javaMailSender;

    /**
     * Конструктор
     *
     * @param emailFrom адрес отправителя
     */
    public EmailSender(@Value("${spring.mail.username}") String emailFrom) {
        this.emailFrom = emailFrom;
    }

    /**
     * Отправляет сообщение
     *
     * @param message данные сообщения
     */
    public void send(EmailMessage message) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(emailFrom);
        simpleMailMessage.setSubject(message.getSubject());
        simpleMailMessage.setText(message.getText());
        simpleMailMessage.setTo(message.getEmail());

        javaMailSender.send(simpleMailMessage);
    }

    /**
     * Сеттер
     *
     * @param javaMailSender javaMailSender
     */
    @Autowired
    public void setJavaMailSender(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }
}
