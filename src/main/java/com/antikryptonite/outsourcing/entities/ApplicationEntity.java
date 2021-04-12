package com.antikryptonite.outsourcing.entities;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Сущность заявки
 */
@Entity
@Table(name = "application")
@Data
public class ApplicationEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "purchase_id")
    private PurchaseEntity purchase;

    @ManyToOne
    @JoinColumn(name = "producer_id")
    private ProducerEntity producer;

    @Column(name = "publication_date")
    private LocalDate publicationDate;

    @Column(name = "price")
    private Long price;

    @Column(name = "description")
    private String description;

    @Column(name = "winner")
    private boolean winner;

    @OneToMany
    @JoinTable(
            name = "application_document",
            joinColumns = @JoinColumn(name = "application_id"),
            inverseJoinColumns = @JoinColumn(name = "document_id")
    )
    private List<DocumentEntity> documents;


}
