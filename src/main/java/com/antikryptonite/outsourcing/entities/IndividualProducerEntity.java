package com.antikryptonite.outsourcing.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

/**
 * Сущность физического лица
 */
@Entity
@Table(name = "individual_producer")
@Data
public class IndividualProducerEntity {
    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "last_name")
    private String lastName;

    @OneToOne
    @JoinColumn(name = "producer_id")
    private ProducerEntity producer;
}
