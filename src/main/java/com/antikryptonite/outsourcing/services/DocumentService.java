package com.antikryptonite.outsourcing.services;

import com.antikryptonite.outsourcing.dto.response.DocumentMetaResponse;
import com.antikryptonite.outsourcing.entities.*;
import com.antikryptonite.outsourcing.exceptions.*;
import com.antikryptonite.outsourcing.repositories.*;
import com.ibm.icu.text.Transliterator;
import lombok.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Сервис для работы с файлами документов
 */
@Service
@Transactional
public class DocumentService {
    private String documentPath = "";

    private final DocumentRepository documentRepository;

    /**
     * Конструктор
     */
    @Autowired
    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    /**
     * Метод для загрузки файлов
     *
     * @param file файл
     */
    public DocumentMetaResponse loadDocument(MultipartFile file, DocumentType documentType) throws ApplicationException {
        try {
            Role role = SecurityUtil.getRole();
            switch (documentType) {
                case ACCREDITATION_APPLY:
                    if (role != Role.USER) {
                        throw new AccessDeniedException("Only for USER");
                    }
                    break;
                case PURCHASE_APPLY:
                    if (role != Role.PRODUCER) {
                        throw new AccessDeniedException("Only for PRODUCER");
                    }
                    break;
                case PURCHASE_START:
                case PURCHASE_FINISH:
                    if (role != Role.SALESMAN && role != Role.ADMIN) {
                        throw new AccessDeniedException("Only for SALESMAN or ADMIN");
                    }
                    break;
            }

            UserEntity owner = new UserEntity();
            UUID ownerId = SecurityUtil.getUserId();
            owner.setId(ownerId);

            checkDirectory();
            UUID name = UUID.randomUUID();
            Files.copy(file.getInputStream(), Paths.get(documentPath + name.toString()), StandardCopyOption.REPLACE_EXISTING);

            DocumentEntity documentEntity = new DocumentEntity();
            documentEntity.setId(name);
            documentEntity.setOwner(owner);
            documentEntity.setPath(documentPath + name.toString());
            Transliterator transliterator = Transliterator.getInstance("Russian-Latin/BGN");
            documentEntity.setDocumentType(documentType);
            documentEntity.setName(transliterator.transliterate(Objects.requireNonNull(file.getOriginalFilename())));
            documentEntity.setSize(file.getSize());
            documentRepository.save(documentEntity);
            return convertToResponse(documentEntity);
        } catch (IOException ex) {
            throw new ApplicationException("File saving exception", ex);
        }
    }

    /**
     * Получения файла
     *
     * @param fileId id файла
     * @return InputStream файла
     */
    public Document getDocument(UUID fileId) throws ApplicationException {
        DocumentEntity documentEntity =
                documentRepository.findById(fileId).orElseThrow(() -> new ResourceNotFoundException(String.format("Document %s", fileId)));

        switch (documentEntity.getDocumentType()) {

            case ACCREDITATION_APPLY:
                if (!checkAccreditationApplyAccess(documentEntity)) {
                    throw new AccessDeniedException("Access denied");
                }
                break;
            case PURCHASE_START:
            case PURCHASE_FINISH:
            case PURCHASE_APPLY:
                break;
        }

        try {
            return new Document(Files.readAllBytes(Paths.get(documentEntity.getPath())), documentEntity.getName());
        } catch (IOException ex) {
            throw new ApplicationException("File getting exception", ex);
        }
    }

    private static boolean checkAccreditationApplyAccess(DocumentEntity documentEntity) {
        UUID userId = SecurityUtil.getUserId();
        Role role = SecurityUtil.getRole();

        return userId.equals(documentEntity.getOwner().getId()) || role == Role.ADMIN || role == Role.SALESMAN || role == Role.LAWYER;
    }
    /**
     * Получение метаинформации
     *
     * @param fileId id документа
     * @return DocumentMetaResponse
     */
    public DocumentMetaResponse getMeta(UUID fileId) throws ApplicationException {
        DocumentEntity documentEntity =
                documentRepository.findById(fileId).orElseThrow(() -> new ResourceNotFoundException(String.format("Document %s", fileId)));
        return convertToResponse(documentEntity);
    }

    private static DocumentMetaResponse convertToResponse(DocumentEntity documentEntity) {
        DocumentMetaResponse response = new DocumentMetaResponse();
        response.setId(documentEntity.getId());
        response.setDocumentType(documentEntity.getDocumentType());
        response.setName(documentEntity.getName());
        response.setSize(documentEntity.getSize());
        return response;
    }

    /**
     * @param documentPath путь к директории сохранения файлов
     */
    @Value("${document.path}")
    public void setDocumentPath(String documentPath) {
        this.documentPath = documentPath;
    }

    private void checkDirectory() throws ApplicationException {
        File file = new File(documentPath);
        if (!file.exists()) {
            file.mkdir();
        }
        if (!file.isDirectory()) {
            throw new ApplicationException("Document path is not available for saving documents");
        }
    }


    @Data
    @AllArgsConstructor
    public static class Document {

        private byte[] file;

        private String filename;
    }
}
