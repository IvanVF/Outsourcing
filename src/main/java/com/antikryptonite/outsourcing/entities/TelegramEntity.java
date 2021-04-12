package com.antikryptonite.outsourcing.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

/**
 * Сущность Telegram записи
 */
@Entity
@Table(name = "telegram")
@Data
public class TelegramEntity {
    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "telegram_id")
    private Integer telegramId;

    @ManyToOne
    @JoinColumn(name = "producer_id")
    private ProducerEntity producer;
}
