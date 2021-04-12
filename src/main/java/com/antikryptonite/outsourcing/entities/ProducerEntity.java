package com.antikryptonite.outsourcing.entities;

import lombok.Data;

import javax.persistence.*;
import java.time.*;
import java.util.*;

/**
 * Сущность поставщика
 */
@Entity
@Table(name = "producer")
@Data
public class ProducerEntity {
    @Id
    @Column(name = "id")
    private UUID producerId;

    @Column(name = "inn")
    private String inn;

    @Column(name = "confirm")
    private boolean confirm;

    @Column(name = "confirm_code")
    private UUID confirmCode;

    @Column(name = "accreditation")
    @Enumerated(EnumType.STRING)
    private AccreditationType accreditation;

    @Column(name = "individual")
    private boolean individual;

    @Column(name = "accreditation_request")
    private boolean request;

    @Column(name = "registration_date")
    private LocalDate registrationDate;

    @Column(name = "accreditation_time")
    private LocalDateTime accreditationTime;

    @Column(name = "block_date")
    private LocalDate blockDate;

    @Column(name = "block_comment")
    private String blockComment;

    @Column(name = "legal_address")
    private String legalAddress;

    @Column(name = "actual_address")
    private String actualAddress;

    @Column(name = "url")
    private String url;

    @Column(name = "agencies")
    private String agencies;

    @Column(name = "specialization")
    private String specialization;

    @Column(name = "headcount")
    private Integer headcount;

    @Column(name = "telegram_token")
    private UUID telegramToken;

    @OneToMany(mappedBy = "producer", cascade = CascadeType.ALL)
    private List<TelegramEntity> telegramIds;

    @OneToMany(mappedBy = "producer", cascade = CascadeType.ALL)
    private List<PhoneEntity> phones;

    @OneToMany(mappedBy = "producer", cascade = CascadeType.ALL)
    private List<StaffEntity> staffs;

    @OneToMany(mappedBy = "producer", cascade = CascadeType.ALL)
    private List<PortfolioEntity> portfolios;

    @OneToMany(mappedBy = "producer", cascade = CascadeType.ALL)
    private List<StackEntity> stacks;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @OneToMany(mappedBy = "producer")
    private List<ApplicationEntity> applications;

    @OneToMany
    @JoinTable(
            name = "accreditation_apply_document",
            joinColumns = @JoinColumn(name = "producer_id"),
            inverseJoinColumns = @JoinColumn(name = "document_id")
    )
    private List<DocumentEntity> accreditationDocuments;
}
