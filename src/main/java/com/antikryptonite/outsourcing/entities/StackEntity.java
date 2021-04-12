package com.antikryptonite.outsourcing.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

/**
 * Сущность стека технологий
 */
@Entity
@Table(name = "producer_stack")   //todo: правильно? согласно liquibase
@Data
public class StackEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "technology")
    private String technology;

    @ManyToOne
    @JoinColumn(name = "producer_id")
    private ProducerEntity producer;

}
