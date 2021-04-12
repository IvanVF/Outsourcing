package com.antikryptonite.outsourcing.services;

import com.antikryptonite.outsourcing.dto.request.*;
import com.antikryptonite.outsourcing.dto.response.*;
import com.antikryptonite.outsourcing.entities.*;
import com.antikryptonite.outsourcing.exceptions.*;
import com.antikryptonite.outsourcing.mail.*;
import com.antikryptonite.outsourcing.repositories.*;
import com.antikryptonite.outsourcing.services.paging.OffsetLimitRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис закупок
 */
@Service
@Transactional
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;

    private final PurchaseStackRepository purchaseStackRepository;

    private final ApplicationRepository applicationRepository;

    private final DocumentRepository documentRepository;

    private final PhysicalProducerRepository physicalProducerRepository;

    private final EntityProducerRepository entityProducerRepository;

    private final ProducerRepository producerRepository;
    /**
     * Конструктор
     */
    @Autowired
    public PurchaseService(
            PurchaseRepository purchaseRepository, PurchaseStackRepository purchaseStackRepository,
            ApplicationRepository applicationRepository, DocumentRepository documentRepository,
            ProducerRepository producerRepository,
            PhysicalProducerRepository physicalProducerRepository,
            EntityProducerRepository entityProducerRepository
    ) {
        this.purchaseRepository = purchaseRepository;
        this.purchaseStackRepository = purchaseStackRepository;
        this.applicationRepository = applicationRepository;
        this.documentRepository = documentRepository;
        this.producerRepository = producerRepository;
        this.physicalProducerRepository = physicalProducerRepository;
        this.entityProducerRepository = entityProducerRepository;
    }

    /**
     * Создать закупку
     */
    public PurchaseCreateResponse createPurchase(PurchaseRequest purchaseRequest) throws ResourceNotFoundException, UniqueException {

        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setPurchaseId(UUID.randomUUID());
        purchaseEntity.setName(purchaseRequest.getName());
        purchaseEntity.setDescription(purchaseRequest.getDescription());
        purchaseEntity.setSubject(purchaseRequest.getSubject());
        purchaseEntity.setStartingPrice(purchaseRequest.getStartingPrice());
        purchaseEntity.setCurrency(purchaseRequest.getCurrency());
        purchaseEntity.setStartDate(purchaseRequest.getStartDate());
        purchaseEntity.setFinishDate(purchaseRequest.getFinishDate());
        purchaseEntity.setPublicationDate(LocalDate.now());
        purchaseEntity.setStatus(calculatePurchaseStatus(purchaseEntity));

        if (purchaseRequest.getParent() != null && purchaseRepository.existsById(purchaseRequest.getParent())) {
            PurchaseEntity parent = purchaseRepository.findById(purchaseRequest.getParent()).orElseThrow(IllegalStateException::new);
            if (!parent.isAdditionalStagePossible()) {
                throw new UniqueException("The purchase can not have additional stage");
            }
            purchaseEntity.setAdditionalStagePossible(false);
            purchaseEntity.setRelative(parent);
            purchaseEntity.setRelativeParent(true);
            purchaseRepository.save(purchaseEntity);
            parent.setRelative(purchaseEntity);
            parent.setAdditionalStagePossible(false);
            purchaseRepository.save(parent);
        } else {
            purchaseEntity.setAdditionalStagePossible(true);
            purchaseEntity.setRelativeParent(false);
        }

        if (purchaseRequest.getStartDocuments() != null) {
            List<DocumentEntity> listDocuments = new ArrayList<>();
            for (UUID documentId : purchaseRequest.getStartDocuments()) {
                DocumentEntity documentEntity =
                        documentRepository.findById(documentId).orElseThrow(() -> new ResourceNotFoundException(String.format("Documents %s", documentId)));
                if (documentEntity.getDocumentType() != DocumentType.PURCHASE_START) {
                    throw new AccessDeniedException("Access denied");
                }
                listDocuments.add(documentEntity);
            }
            purchaseEntity.setStartDocuments(listDocuments);
        }

        purchaseEntity = purchaseRepository.save(purchaseEntity);

        for (int i = 0; i < purchaseRequest.getStack().size(); i++) {
            PurchaseStackEntity purchaseStackEntity = new PurchaseStackEntity();
            purchaseStackEntity.setId(UUID.randomUUID());
            purchaseStackEntity.setTechnology(purchaseRequest.getStack().get(i));
            purchaseStackEntity.setPurchase(purchaseEntity);
            purchaseStackRepository.save(purchaseStackEntity);
        }

        PurchaseCreateResponse response = new PurchaseCreateResponse();
        response.setPurchaseId(purchaseEntity.getPurchaseId());
        response.setName(purchaseEntity.getName());
        response.setNumber(purchaseEntity.getNumber());
        response.setDescription(purchaseEntity.getDescription());
        response.setSubject(purchaseEntity.getSubject());
        response.setStartingPrice(purchaseEntity.getStartingPrice());
        response.setCurrency(purchaseEntity.getCurrency());
        response.setStartDate(purchaseEntity.getStartDate());
        response.setFinishDate(purchaseEntity.getFinishDate());
        response.setPublicationDate(purchaseEntity.getPublicationDate());

        response.setStack(getStackList(purchaseEntity));
        response.setAdditionalStagePossible(purchaseEntity.isAdditionalStagePossible());
        response.setStatus(calculatePurchaseStatus(purchaseEntity));

        if (purchaseEntity.getRelative() != null) {
            PurchaseRelativeFieldResponse relative = new PurchaseRelativeFieldResponse();
            relative.setId(purchaseEntity.getRelative().getPurchaseId());
            relative.setParent(purchaseEntity.isRelativeParent());
            response.setRelative(relative);
        }
        response.setStartDocuments(purchaseRequest.getStartDocuments());

        return response;
    }

    /**
     * Показать одну закупку по id
     */
    public PurchaseResponse showPurchase(UUID id) throws ResourceNotFoundException {
        PurchaseEntity entity = purchaseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Purchase %s", id)));
        entity.setStatus(calculatePurchaseStatus(entity));
        purchaseRepository.save(entity);

        PurchaseResponse response = new PurchaseResponse();
        response.setPurchaseId(entity.getPurchaseId());
        response.setName(entity.getName());
        response.setNumber(entity.getNumber());
        response.setDescription(entity.getDescription());
        response.setSubject(entity.getSubject());
        response.setStartingPrice(entity.getStartingPrice());
        response.setCurrency(entity.getCurrency());
        response.setStartDate(entity.getStartDate());
        response.setFinishDate(entity.getFinishDate());
        response.setPublicationDate(entity.getPublicationDate());

        response.setStack(getStackList(entity));
        response.setAdditionalStagePossible(entity.isAdditionalStagePossible());
        response.setStatus(calculatePurchaseStatus(entity).toString());

        PurchaseRelativeFieldResponse relative = new PurchaseRelativeFieldResponse();
        if (entity.getRelative() == null) {
            response.setRelative(null);
        } else {
            relative.setId(entity.getRelative().getPurchaseId());
            relative.setParent(entity.isRelativeParent());
            response.setRelative(relative);
        }

        List<ApplicationEntity> applicationEntities = applicationRepository.findAllByPurchasePurchaseId(entity.getPurchaseId());
        List<BriefProducerInfo> winners = applicationEntities.stream().filter(ApplicationEntity::isWinner).
                map(application -> {
                    ProducerEntity producerEntity = application.getProducer();
                    if (producerEntity.isIndividual()) {
                        IndividualProducerEntity individualProducerEntity = physicalProducerRepository.getByProducer(producerEntity).get();
                        BriefProducerInfo briefProducerInfo = new BriefProducerInfo();
                        briefProducerInfo.setIndividual(true);
                        briefProducerInfo.setFirstName(individualProducerEntity.getFirstName());
                        briefProducerInfo.setMiddleName(individualProducerEntity.getMiddleName());
                        briefProducerInfo.setLastName(individualProducerEntity.getLastName());
                        return briefProducerInfo;
                    } else {
                        EntityProducerEntity entityProducerEntity = entityProducerRepository.getByProducer(producerEntity).get();
                        BriefProducerInfo briefProducerInfo = new BriefProducerInfo();
                        briefProducerInfo.setIndividual(false);
                        briefProducerInfo.setOrgName(entityProducerEntity.getOrganizationName());
                        return briefProducerInfo;
                    }
                }).collect(Collectors.toList());
        response.setWinners(winners);

        response.setClosingDescription(entity.getClosingDescription());

        List<UUID> docsResponse = entity.getStartDocuments().stream().map(DocumentEntity::getId).collect(Collectors.toList());
        response.setStartDocuments(docsResponse);

        List<UUID> docResponse = entity.getFinishDocuments().stream().map(DocumentEntity::getId).collect(Collectors.toList());
        response.setFinishDocuments(docResponse);

        return response;
    }

    /**
     * Показать список закупок с возможностью фильтрации и пагинации
     *
     * @param status - статус запрашиваемых закупок (default = ALL)
     * @param limit, offset - параметры пагинации
     */
    public List<PurchaseListResponse> showPurchases(StatusPurchase status, int limit, int offset) {
        if (status == null) {
            status = StatusPurchase.ALL;
        }

        final StatusPurchase stat = status;
        List<PurchaseEntity> listOfEntity =
                purchaseRepository.findAll(new OffsetLimitRequest(offset, limit, Sort.unsorted())).stream().collect(Collectors.toList());

        return listOfEntity.stream().filter(entity -> {
            if (stat == StatusPurchase.ALL) {
                return true;
            }
            return stat == calculatePurchaseStatus(entity);
        }).map(entity -> {
            PurchaseListResponse response = new PurchaseListResponse();
            response.setPurchaseId(entity.getPurchaseId());
            response.setName(entity.getName());
            response.setNumber(entity.getNumber());
            response.setDescription(entity.getDescription());
            response.setStartingPrice(entity.getStartingPrice());
            response.setCurrency(entity.getCurrency());
            response.setStartDate(entity.getStartDate());
            response.setFinishDate(entity.getFinishDate());
            response.setPublicationDate(entity.getPublicationDate());
            response.setClosingDate(entity.getClosingDate());

            List<String> stack = getStackList(entity);
            response.setStack(stack);
            response.setAdditionalStagePossible(entity.isAdditionalStagePossible());
            response.setStatus(calculatePurchaseStatus(entity).toString());

            return response;
        }).collect(Collectors.toList());
    }

    /**
     * Закрыть/завершить закупку
     */
    public void closePurchase(UUID id, PurchaseCloseRequest request) throws ApplicationException {
        PurchaseEntity purchase = purchaseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Purchase %s", id)));
        if (request.getStatus() != StatusPurchase.CANCELED && request.getStatus() != StatusPurchase.FINISHED) {
            throw new ValidationFallsException("Status must be CANCELED or FINISHED");
        }

        if (request.getStatus() == StatusPurchase.CANCELED) {
            purchase.setStatus(StatusPurchase.CANCELED);
        } else if (request.getStatus() == StatusPurchase.FINISHED) {
            purchase.setStatus(StatusPurchase.FINISHED);
            for (UUID winnerApplicationId : request.getWinners()) {
                Optional<ApplicationEntity> applicationEntity = applicationRepository.findById(winnerApplicationId);
                if (!applicationEntity.isPresent()) {
                    throw new ResourceNotFoundException(String.format("Application %s", id));
                }
                applicationEntity.get().setWinner(true);
                applicationRepository.save(applicationEntity.get());
            }
        }

        purchase.setClosingDescription(request.getClosingDescription());
        if (request.getFinishDocuments() != null) {
            List<DocumentEntity> listDocuments = new ArrayList<>();
            for (UUID documentId : request.getFinishDocuments()) {
                DocumentEntity documentEntity =
                        documentRepository.findById(documentId).orElseThrow(() -> new ResourceNotFoundException(String.format("Documents %s", documentId)));
                if (documentEntity.getDocumentType() != DocumentType.PURCHASE_FINISH) {
                    throw new AccessDeniedException("Access denied");
                }
                listDocuments.add(documentEntity);
            }
            purchase.setFinishDocuments(listDocuments);
        }

        purchaseRepository.save(purchase);
    }

    public StatusPurchase calculatePurchaseStatus(PurchaseEntity entity) {

        if (entity.getStatus() != StatusPurchase.CANCELED && entity.getStatus() != StatusPurchase.FINISHED) {
            if ((entity.getStartDate().isBefore(LocalDate.now()) || entity.getStartDate().isEqual(LocalDate.now())) &&
                    entity.getFinishDate().isAfter(LocalDate.now())) {
                entity.setStatus(StatusPurchase.OPENED);
                purchaseRepository.save(entity);
            }
            if (entity.getFinishDate().isBefore(LocalDate.now())) {
                entity.setStatus(StatusPurchase.CLOSED);
                purchaseRepository.save(entity);
            }
            if (entity.getStartDate().isAfter(LocalDate.now())) {
                entity.setStatus(StatusPurchase.PUBLISHED);
                purchaseRepository.save(entity);
            }
        }
        return entity.getStatus();
    }

    private List<String> getStackList(PurchaseEntity entity) {
        List<PurchaseStackEntity> fullStack = purchaseStackRepository.findAllByPurchasePurchaseId(entity.getPurchaseId());
        return fullStack.stream().map(PurchaseStackEntity::getTechnology).collect(Collectors.toList());
    }
}
