package com.antikryptonite.outsourcing.entities;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Сущность портфолио
 */
@Entity
@Table(name = "portfolio")
@Data
public class PortfolioEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "customer")
    private String customer;

    @Column(name = "description")
    private String description;

    @Column(name = "url")
    private String url;

    @ManyToOne
    @JoinColumn(name = "producer_id")
    private ProducerEntity producer;

}
