package com.antikryptonite.outsourcing.entities;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Сущность истории действий
 */
@Entity
@Table(name = "audit")
@Data
public class AuditEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "action_time")
    private LocalDateTime actionTime;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "action")
    private String action;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

}
