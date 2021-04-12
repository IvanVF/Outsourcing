package com.antikryptonite.outsourcing.controllers;

import com.antikryptonite.outsourcing.dto.response.DocumentMetaResponse;
import com.antikryptonite.outsourcing.entities.DocumentType;
import com.antikryptonite.outsourcing.exceptions.ApplicationException;
import com.antikryptonite.outsourcing.services.DocumentService;
import com.antikryptonite.outsourcing.validation.DocumentConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

/**
 * Контроллер для работы с документами
 */
@RestController
@Validated
public class DocumentController {

    private final DocumentService documentService;

    @Autowired
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/document")
    @PreAuthorize("isAuthenticated()")
    public DocumentMetaResponse loadDocument(@RequestParam("document") @DocumentConstraint MultipartFile file, @RequestParam("type") DocumentType documentType)
            throws ApplicationException {
        return documentService.loadDocument(file, documentType);
    }

    @GetMapping("/document/{id}/meta")
    public DocumentMetaResponse getMeta(@PathVariable("id") UUID fileId) throws ApplicationException {
        return documentService.getMeta(fileId);
    }

    @GetMapping(path = "/document/{id}")
    public ResponseEntity<?> getDocument(@PathVariable("id") UUID fileId) throws ApplicationException {
        DocumentService.Document document = documentService.getDocument(fileId);
        return ResponseEntity.ok().contentType(MediaTypeFactory.getMediaType(document.getFilename()).orElse(MediaType.MULTIPART_FORM_DATA)).header(
                HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getFilename() + "\"").body(document.getFile());
    }
}
