package com.antikryptonite.outsourcing.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

/**
 * Сущность стека технологий для закупки
 */
@Entity
@Table(name = "purchase_stack")
@Data
public class PurchaseStackEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "purchase_id")
    private PurchaseEntity purchase;

    @Column(name = "technology")
    private String technology;
}
