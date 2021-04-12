package com.antikryptonite.outsourcing.entities;

import lombok.Data;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.*;

/**
 * Сущность закупки
 */
@Entity
@Table(name = "purchase")
@Data
public class PurchaseEntity {

    @Id
    @Column(name = "id")
    private UUID purchaseId;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "subject")
    private String subject;

    @Column(name = "starting_price")
    private Long startingPrice;

    @Column(name = "currency")
    @Enumerated(EnumType.STRING)
    private CurrencyValue currency;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "finish_date")
    private LocalDate finishDate;

    @Column(name = "publication_date")
    private LocalDate publicationDate;

    @Column(name = "closing_date")
    private LocalDate closingDate;

    @Column(name = "additional_stage_possible")
    private boolean additionalStagePossible;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatusPurchase status;

    @Generated(GenerationTime.ALWAYS)
    @Column(name = "number",  insertable = false, updatable = false)
    private Integer number;

    @Column(name = "closing_description")
    private String closingDescription;

    @JoinColumn(name = "relative_id")
    @OneToOne
    private PurchaseEntity relative;

    @Column(name = "relative_parent")
    private boolean relativeParent;

    @OneToMany
    @JoinTable(
            name = "purchase_start_document",
            joinColumns = @JoinColumn(name = "purchase_id"),
            inverseJoinColumns = @JoinColumn(name = "document_id")
    )
    private List<DocumentEntity> startDocuments;

    @OneToMany
    @JoinTable(
            name = "purchase_finish_document",
            joinColumns = @JoinColumn(name = "purchase_id"),
            inverseJoinColumns = @JoinColumn(name = "document_id")
    )
    private List<DocumentEntity> finishDocuments;

    @OneToMany(mappedBy = "purchase")
    private List<ApplicationEntity> applications;
}