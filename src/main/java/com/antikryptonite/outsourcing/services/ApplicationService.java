package com.antikryptonite.outsourcing.services;

import com.antikryptonite.outsourcing.dto.request.ApplicationRequest;
import com.antikryptonite.outsourcing.dto.response.*;
import com.antikryptonite.outsourcing.entities.*;
import com.antikryptonite.outsourcing.exceptions.*;
import com.antikryptonite.outsourcing.repositories.*;
import com.antikryptonite.outsourcing.services.paging.OffsetLimitRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис заявок
 */
@Service
@Transactional
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    private final PurchaseRepository purchaseRepository;

    private final DocumentRepository documentRepository;

    private final EntityProducerRepository entityProducerRepository;

    private final PhysicalProducerRepository individualProducerRepository;

    private ProducerRepository producerRepository;

    /**
     * Конструктор
     */
    @Autowired
    public ApplicationService(
            ApplicationRepository applicationRepository, PurchaseRepository purchaseRepository,
            DocumentRepository documentRepository,
            EntityProducerRepository entityProducerRepository,
            PhysicalProducerRepository individualProducerRepository
    ) {
        this.applicationRepository = applicationRepository;
        this.purchaseRepository = purchaseRepository;
        this.documentRepository = documentRepository;
        this.entityProducerRepository = entityProducerRepository;
        this.individualProducerRepository = individualProducerRepository;
    }

    /**
     * Создать заявку
     *
     * @param applicationRequest заявка
     * @param purchaseId         id закупки
     */
    public ApplicationResponse createApplication(ApplicationRequest applicationRequest, UUID purchaseId) throws ApplicationException {
        PurchaseEntity purchaseEntity =
                purchaseRepository.findById(purchaseId).orElseThrow(() -> new ResourceNotFoundException(String.format("Purchase %s", purchaseId)));

        UUID userId = SecurityUtil.getUserId();

        if (purchaseEntity.getApplications().stream().anyMatch(application -> application.getProducer().getUser().getId().equals(userId))) {
            throw new UniqueException("Application for the purchase from the user is already published");
        }

        ApplicationEntity applicationEntity = new ApplicationEntity();
        applicationEntity.setId(UUID.randomUUID());
        applicationEntity.setWinner(false);
        applicationEntity.setDescription(applicationRequest.getDescription());
        applicationEntity.setPurchase(purchaseEntity);
        applicationEntity.setPublicationDate(LocalDate.now());
        applicationEntity.setPrice(applicationRequest.getPrice());

        ProducerEntity producer =
                producerRepository.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException(String.format("Producer %s", userId)));
        applicationEntity.setProducer(producer);

        if (applicationRequest.getDocuments() == null) {
            throw new ValidationFallsException("No documents");
        }
        List<DocumentEntity> documents = new ArrayList<>();
        for (UUID documentId : applicationRequest.getDocuments()) {
            DocumentEntity documentEntity =
                    documentRepository.findById(documentId).orElseThrow(() -> new ResourceNotFoundException(String.format("Document %s", documentId)));
            if (!documentEntity.getOwner().getId().equals(userId)) {
                throw new AccessDeniedException("Access denied. Some document doesn't belong to this producer");
            }
            if (!documentEntity.getDocumentType().equals(DocumentType.PURCHASE_APPLY)) {
                throw new AccessDeniedException("Access denied. Invalid document type");
            }
            documents.add(documentEntity);
        }
        applicationEntity.setDocuments(documents);

        applicationRepository.save(applicationEntity);

        return convertEntityToApplication(applicationEntity);
    }

    /**
     * Получить заявку
     *
     * @param applicationId id заявки
     * @param purchaseId    id закупки
     * @return заявка
     */
    public ApplicationResponse getApplication(UUID applicationId, UUID purchaseId) throws ApplicationException {
        ApplicationEntity application =
                applicationRepository.findById(applicationId).orElseThrow(() -> new ResourceNotFoundException(String.format("Application %s", purchaseId)));
        if (!application.getPurchase().getPurchaseId().equals(purchaseId)) {
            throw new ResourceNotFoundException(String.format("Purchase %s", purchaseId));
        }

        UUID userId = SecurityUtil.getUserId();
        if (application.getProducer().getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Access denied");
        }

        return convertEntityToApplication(application);
    }

    /**
     * Получить все заявки
     *
     * @param applicationType тип заявки
     * @param purchaseId    id закупки
     * @param limit ограничение вывода на страницу
     * @param offset
     * @return список заявок
     */

    public List<ListApplicationResponse> getApplications(UUID purchaseId, ApplicationType applicationType, int limit, int offset) throws ApplicationException {
        if (applicationType == null) {
            applicationType = ApplicationType.ALL;
        }

        final ApplicationType type = applicationType;
        List<ApplicationEntity> applications =
                applicationRepository.findAll(new OffsetLimitRequest(offset, limit, Sort.unsorted())).stream().collect(Collectors.toList());

        return applications.stream().filter(application -> {
            if (type == ApplicationType.ALL) {
                return true;
            }

            return application.getProducer().isIndividual() == (type == ApplicationType.INDIVIDUAL);
        }).filter(application -> application.getPurchase().getPurchaseId().equals(purchaseId)).
                map(application -> {
                    ProducerEntity producerEntity = application.getProducer();
                    ListApplicationResponse response = new ListApplicationResponse();
                    response.setId(application.getId());
                    response.setDescription(application.getDescription());
                    response.setPrice(application.getPrice());
                    response.setPublicationDate(application.getPublicationDate());
                    response.setProducerId(producerEntity.getProducerId());
                    response.setInn(producerEntity.getInn());
                    response.setDocuments(application.getDocuments().stream().map(DocumentEntity::getId).collect(Collectors.toList()));

                    if (producerEntity.isIndividual()) {
                        IndividualProducerEntity individualProducer = individualProducerRepository.getByProducer(producerEntity).orElseThrow(
                                IllegalStateException::new);
                        response.setFirstName(individualProducer.getFirstName());
                        response.setMiddleName(individualProducer.getMiddleName());
                        response.setLastName(individualProducer.getLastName());
                        response.setIndividual(true);
                    } else {
                        EntityProducerEntity entityProducerEntity =
                                entityProducerRepository.getByProducer(producerEntity).orElseThrow(IllegalStateException::new);
                        response.setIndividual(false);
                        response.setOrgName(entityProducerEntity.getOrganizationName());
                        response.setFirstName(entityProducerEntity.getFirstName());
                        response.setLastName(entityProducerEntity.getLastName());
                        response.setMiddleName(entityProducerEntity.getMiddleName());
                    }
                    return response;
                }).collect(Collectors.toList());
    }

    /**
     * Получить все заявки конкретного поставщика
     */
    public List<ApplicationProducerResponse> getProducerApplications(UUID userId) throws ApplicationException {
        ProducerEntity producerEntity = producerRepository.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException(String.format("Purchase %s", userId)));
        List<ApplicationEntity> applicationList = applicationRepository.findAllByProducerProducerId(producerEntity.getProducerId());

        return applicationList.stream().map(applicationEntity -> {
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            try {
                purchaseEntity = purchaseRepository.findById(applicationEntity.getPurchase().getPurchaseId()).orElseThrow(
                        () -> new ResourceNotFoundException(String.format("Purchase %s", applicationEntity.getPurchase().getPurchaseId())));
            } catch (ResourceNotFoundException e) {
                e.printStackTrace();
            }

            ApplicationProducerResponse response = new ApplicationProducerResponse();
            response.setApplicationId(applicationEntity.getId());
            response.setDescription(applicationEntity.getDescription());
            response.setDocuments(applicationEntity.getDocuments().stream().map(DocumentEntity::getId).collect(Collectors.toList()));
            response.setPrice(applicationEntity.getPrice());
            response.setProducerId(applicationEntity.getProducer().getProducerId());
            response.setPublicationDate(applicationEntity.getPublicationDate());
            response.setPurchaseId(purchaseEntity.getPurchaseId());
            response.setPurchaseName(purchaseEntity.getName());
            return response;
        }).collect(Collectors.toList());
    }

    private static ApplicationResponse convertEntityToApplication(ApplicationEntity entity) {
        ApplicationResponse applicationResponse = new ApplicationResponse();
        applicationResponse.setId(entity.getId());
        applicationResponse.setPrice(entity.getPrice());
        applicationResponse.setDescription(entity.getDescription());
        applicationResponse.setDocuments(entity.getDocuments().stream().map(DocumentEntity::getId).collect(Collectors.toList()));
        applicationResponse.setPublicationDate(entity.getPublicationDate());
        applicationResponse.setProducerId(entity.getProducer().getProducerId());

        return applicationResponse;
    }

    /**
     * Сеттер producerEntity
     */
    @Autowired
    public void setProducerRepository(ProducerRepository producerRepository) {
        this.producerRepository = producerRepository;
    }
}
