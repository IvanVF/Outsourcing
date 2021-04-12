package com.antikryptonite.outsourcing.dto.response;

import com.antikryptonite.outsourcing.entities.DocumentType;
import lombok.Data;

import java.util.UUID;

/**
 * Тело ответа метаинформации о документе
 */
@Data
public class DocumentMetaResponse {
    private UUID id;

    private DocumentType documentType;

    private String name;

    private long size;
}
