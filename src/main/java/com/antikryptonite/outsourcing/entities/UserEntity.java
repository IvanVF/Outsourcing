package com.antikryptonite.outsourcing.entities;

import lombok.Data;
import javax.persistence.*;
import java.util.*;

/**
 * Сущность пользователя
 */
@Entity
@Table(name = "user_table")
@Data
public class UserEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "email")
    private String login;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user")
    private List<RefreshTokenEntity> refreshTokens;
}