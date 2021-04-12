package com.antikryptonite.outsourcing.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

/**
 * Сущность для юридического лица
 */
@Entity
@Table(name = "entity_producer")
@Data
public class EntityProducerEntity {
    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "organization_name")
    private String organizationName;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "position")
    private String position;

    @OneToOne
    @JoinColumn(name = "producer_id")
    private ProducerEntity producer;
}
