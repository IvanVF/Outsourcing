package com.antikryptonite.outsourcing.entities;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "refresh_session")
@Data
public class RefreshTokenEntity {
    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "refresh_token")
    private UUID refreshToken;

    @Column(name = "fingerprint")
    private String fingerprint;

    @Column(name = "exp_time")
    private LocalDateTime expTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
