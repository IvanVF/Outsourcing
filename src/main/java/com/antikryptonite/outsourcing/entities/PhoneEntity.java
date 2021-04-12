package com.antikryptonite.outsourcing.entities;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.UUID;

/**
 * Сущность телефонных номеров
 */
@Entity
@Table(name = "phone")
@Data
public class PhoneEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "phone")
    private Long phone;

    @ManyToOne
    @JoinColumn(name = "producer_id")
    private ProducerEntity producer;

}
