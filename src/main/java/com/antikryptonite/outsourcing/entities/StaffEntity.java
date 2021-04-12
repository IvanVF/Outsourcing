package com.antikryptonite.outsourcing.entities;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Сущность команды
 */
@Entity
@Table(name = "staff")
@Data
public class StaffEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @NotNull
    @Column(name = "headcount")
    private int headCount;

    @NotNull
    @Column(name = "activity")
    private String activity;

    @ManyToOne
    @JoinColumn(name = "producer_id")
    private ProducerEntity producer;
}
