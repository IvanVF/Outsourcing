package com.antikryptonite.outsourcing.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

/**
 * Сущность работника
 */
@Entity
@Table(name = "worker")
@Data
public class WorkerEntity {
    @Id
    @Column(name = "id")
    private UUID workerId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "last_name")
    private String lastName;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
