package com.antikryptonite.outsourcing.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

/**
 * Сущность документа
 */
@Entity
@Table(name = "document")
@Data
public class DocumentEntity {
    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "address")
    private String path;

    @Column(name = "name")
    private String name;

    @Column(name = "size")
    private Long size;

    @ManyToOne
    @JoinColumn(name = "owner")
    private UserEntity owner;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private DocumentType documentType;
}
