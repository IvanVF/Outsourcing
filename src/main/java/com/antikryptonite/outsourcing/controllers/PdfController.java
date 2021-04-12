package com.antikryptonite.outsourcing.controllers;

import com.antikryptonite.outsourcing.exceptions.ResourceNotFoundException;
import com.antikryptonite.outsourcing.services.PdfService;
import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.UUID;

/**
 * Контроллер для работы с генерируемыми pdf документами
 */
@RestController
public class PdfController {

    private final PdfService pdfService;

    /**
     * Конструктор
     */
    @Autowired
    public PdfController(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    /**
     * GET-запрос на получение карточки поставщика по его userId
     */
    @GetMapping("/pdf/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_LAWYER', 'ROLE_SALESMAN')")
    public ResponseEntity<ByteArrayResource> createProducerInfoPdf(@PathVariable ("id") UUID id) throws ResourceNotFoundException, IllegalArgumentException, IOException, DocumentException {
        return pdfService.createPdf(id);
    }


}
