package com.antikryptonite.outsourcing.services;

import com.antikryptonite.outsourcing.controllers.enums.State;
import com.antikryptonite.outsourcing.dto.request.ApplyIndividualRequest;
import com.antikryptonite.outsourcing.dto.request.ApplyEntityRequest;
import com.antikryptonite.outsourcing.dto.request.BlockRequest;
import com.antikryptonite.outsourcing.dto.response.*;
import com.antikryptonite.outsourcing.entities.*;
import com.antikryptonite.outsourcing.exceptions.*;
import com.antikryptonite.outsourcing.mail.*;
import com.antikryptonite.outsourcing.repositories.*;
import com.antikryptonite.outsourcing.validation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис поставщика
 */
@Service
@Transactional
public class ProducerService {

    private final UserRepository userRepository;

    private final ProducerRepository producerRepository;

    private final EntityProducerRepository entityProducerRepository;

    private final PhysicalProducerRepository physicalProducerRepository;

    private final PhoneRepository phoneRepository;

    private final PortfolioRepository portfolioRepository;

    private final StackRepository stackRepository;

    private final StaffRepository staffRepository;

    private DocumentRepository documentRepository;

    /**
     * Конструктор сервиса
     */
    @Autowired
    public ProducerService(
            UserRepository userRepository, ProducerRepository producerRepository,
            EntityProducerRepository entityProducerRepository,
            PhysicalProducerRepository physicalRepository,
            PhoneRepository phoneRepository,
            PortfolioRepository portfolioRepository,
            StackRepository stackRepository,
            StaffRepository staffRepository
    ) {
        this.userRepository = userRepository;
        this.producerRepository = producerRepository;
        this.entityProducerRepository = entityProducerRepository;
        physicalProducerRepository = physicalRepository;
        this.phoneRepository = phoneRepository;
        this.portfolioRepository = portfolioRepository;
        this.stackRepository = stackRepository;
        this.staffRepository = staffRepository;
    }

    /**
     * Метод для получения списка поставщиков
     *
     * @return - возвращает список поставщиков
     */
    public ProducersResponse showProducers(State state, String stack) {
        if (state == null) {
            state = State.UNACCREDITED;
        }

        List<ProducerEntity> producersAll = null;
        switch (state) {
            case ACCREDITED:
                producersAll = producerRepository.getAccreditedProducers();
                break;
            case BLOCK:
                producersAll = producerRepository.getBlockProducers();
                break;
            case ALL:
                producersAll = producerRepository.getUnblockProducers();
                break;
            case SILENT:
                producersAll = producerRepository.getSilentProducers();
                break;
            case OVERALL:
                producersAll = producerRepository.getOverallProducers();
                break;
            case UNACCREDITED:
                producersAll = producerRepository.getUnaccreditedProducers();
                break;
        }

        if (stack != null && !stack.isEmpty()) {
            producersAll = producersAll.
                    stream().
                    filter(producer -> producer.getStacks().stream().anyMatch(stackEntity ->
                            stackEntity.getTechnology().equals(stack)
                    )).
                    collect(Collectors.toList());
        }

        List<ProducerResponse> producers = producersAll.stream().map(this::convertToProducerResponse).collect(Collectors.toList());

        ProducersResponse producersResponse = new ProducersResponse();
        producersResponse.setProducersList(producers);
        return producersResponse;
    }

    /**
     * Метод для получения одного поставщика
     *
     * @return - возвращает одного поставщика
     */
    public ProducerFullResponse showOneProducer(UUID id) throws ResourceNotFoundException {
        ProducerEntity producer = producerRepository.findByUserId(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Producer %s", id)));
        return convertToProducerFullResponse(producer);
    }

    /**
     * Метод устанавливает аккредитацию поставщика
     */
    public void accreditProducer(UUID id) throws ResourceNotFoundException {
        ProducerEntity producerEntity = producerRepository.findByUserId(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Producer %s", id)));
        producerEntity.setAccreditation(AccreditationType.ACCREDITED);
        producerEntity.setAccreditationTime(LocalDateTime.now());
        producerEntity.getUser().setRole(Role.PRODUCER);
        producerRepository.save(producerEntity);
    }

    /**
     * Метод подачи запроса на аккредитацию юридического поставщикка
     */
    public void callForEntityProducerAccreditation(UUID id, ApplyEntityRequest applyEntityRequest) throws ApplicationException {

        ProducerEntity producerEntity = producerRepository.findByUserId(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Producer %s", id)));
        if (producerEntity.isIndividual()) {
            throw new ValidationFallsException("The producer is not entity");
        }

        List<Boolean> mistakesList = new ArrayList<>();

        if (!producerEntity.isIndividual()) {
            mistakesList.add(ContactPersonResponseValidator.validateContactPersonResponse(applyEntityRequest.getContactPerson()));
        }
        mistakesList.add(StaffResponseListValidator.validateStaffResponseList(applyEntityRequest.getStaff()));
        mistakesList.add(PortfolioResponseListValidator.validatePortfolioResponseList(applyEntityRequest.getPortfolio()));

        List<Boolean> isAnyMistakes = mistakesList.stream().filter(u -> u.equals(true)).collect(Collectors.toList());

        if (isAnyMistakes.isEmpty()) {
            if (applyEntityRequest.getPhone() != null) {
                if (phoneRepository.existsByPhone(applyEntityRequest.getPhone())) {
                    throw new UniqueException("Phone is not unique");
                }
                PhoneEntity phoneEntity = new PhoneEntity();
                phoneEntity.setId(UUID.randomUUID());
                phoneEntity.setPhone(applyEntityRequest.getPhone());
                phoneEntity.setProducer(producerEntity);
                phoneRepository.save(phoneEntity);
            }
            for (int i = 0; i < applyEntityRequest.getStack().size(); i++) {
                StackEntity stackEntity = new StackEntity();
                stackEntity.setId(UUID.randomUUID());
                stackEntity.setTechnology(applyEntityRequest.getStack().get(i));
                stackEntity.setProducer(producerEntity);
                stackRepository.save(stackEntity);
            }

            if (!producerEntity.isIndividual()) {
                EntityProducerEntity entityProducerEntity = entityProducerRepository.getByProducer(producerEntity).orElseThrow(
                        () -> new ResourceNotFoundException(String.format("Producer %s", id)));
                entityProducerEntity.setFirstName(applyEntityRequest.getContactPerson().getFirstName());
                entityProducerEntity.setLastName(applyEntityRequest.getContactPerson().getLastName());
                entityProducerEntity.setMiddleName(applyEntityRequest.getContactPerson().getMiddleName());
                entityProducerEntity.setPosition(applyEntityRequest.getContactPerson().getPosition());
                entityProducerEntity.setProducer(producerEntity);
                entityProducerRepository.save(entityProducerEntity);
            }

            producerEntity.setUrl(applyEntityRequest.getUrl());
            producerEntity.setLegalAddress(applyEntityRequest.getLegalAddress());
            producerEntity.setActualAddress(applyEntityRequest.getActualAddress());
            producerEntity.setHeadcount(applyEntityRequest.getHeadcount());

            for (int i = 0; i < applyEntityRequest.getStaff().size(); i++) {
                StaffEntity staffEntity = new StaffEntity();
                staffEntity.setId(UUID.randomUUID());
                staffEntity.setActivity(applyEntityRequest.getStaff().get(i).getActivity());
                staffEntity.setHeadCount(applyEntityRequest.getStaff().get(i).getHeadcount());
                staffEntity.setProducer(producerEntity);
                staffRepository.save(staffEntity);
            }

            producerEntity.setSpecialization(applyEntityRequest.getSpecialization());
            producerEntity.setRequest(true);

            for (int i = 0; i < applyEntityRequest.getPortfolio().size(); i++) {
                PortfolioEntity portfolioEntity = new PortfolioEntity();
                portfolioEntity.setId(UUID.randomUUID());
                portfolioEntity.setCustomer(applyEntityRequest.getPortfolio().get(i).getCustomer());
                portfolioEntity.setDescription(applyEntityRequest.getPortfolio().get(i).getDescription());
                portfolioEntity.setUrl(applyEntityRequest.getPortfolio().get(i).getUrl());
                portfolioEntity.setProducer(producerEntity);
                portfolioRepository.save(portfolioEntity);
            }

            if (applyEntityRequest.getDocuments() != null) {
                List<DocumentEntity> documentEntities = new ArrayList<>();
                for (UUID documentId : applyEntityRequest.getDocuments()) {
                    DocumentEntity documentEntity =
                            documentRepository.findById(documentId).orElseThrow(() -> new ResourceNotFoundException(String.format("Document %s", documentId)));
                    if (documentEntity.getDocumentType() != DocumentType.ACCREDITATION_APPLY) {
                        throw new AccessDeniedException("Access denied");
                    }
                    documentEntities.add(documentEntity);
                }
                producerEntity.setAccreditationDocuments(documentEntities);
            }
            producerEntity.setAgencies(applyEntityRequest.getAgencies());
            producerRepository.save(producerEntity);
        }
    }

    /**
     * Метод подачи запроса на аккредитацию физического поставщикка
     */
    public void callForProducerIndividualAccreditation(UUID id, ApplyIndividualRequest applyIndividualRequest) throws ApplicationException {

        ProducerEntity producerEntity = producerRepository.findByUserId(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Producer %s", id)));
        if (!producerEntity.isIndividual()) {
            throw new ValidationFallsException("The producer is not individual");
        }

        List<Boolean> mistakesList = new ArrayList<>();

        mistakesList.add(PortfolioResponseListValidator.validatePortfolioResponseList(applyIndividualRequest.getPortfolio()));

        List<Boolean> isAnyMistakes = mistakesList.stream().filter(u -> u.equals(true)).collect(Collectors.toList());

        if (isAnyMistakes.isEmpty()) {

            if (applyIndividualRequest.getPhone() != null) {
                if (phoneRepository.existsByPhone(applyIndividualRequest.getPhone())) {
                    throw new UniqueException("Phone is not unique");
                }
                PhoneEntity phoneEntity = new PhoneEntity();
                phoneEntity.setId(UUID.randomUUID());
                phoneEntity.setPhone(applyIndividualRequest.getPhone());
                phoneEntity.setProducer(producerEntity);
                phoneRepository.save(phoneEntity);
            }

            for (int i = 0; i < applyIndividualRequest.getStack().size(); i++) {
                StackEntity stackEntity = new StackEntity();
                stackEntity.setId(UUID.randomUUID());
                stackEntity.setTechnology(applyIndividualRequest.getStack().get(i));
                stackEntity.setProducer(producerEntity);
                stackRepository.save(stackEntity);
            }

            producerEntity.setUrl(applyIndividualRequest.getUrl());

            producerEntity.setSpecialization(applyIndividualRequest.getSpecialization());
            producerEntity.setRequest(true);

            for (int i = 0; i < applyIndividualRequest.getPortfolio().size(); i++) {
                PortfolioEntity portfolioEntity = new PortfolioEntity();
                portfolioEntity.setId(UUID.randomUUID());
                portfolioEntity.setCustomer(applyIndividualRequest.getPortfolio().get(i).getCustomer());
                portfolioEntity.setDescription(applyIndividualRequest.getPortfolio().get(i).getDescription());
                portfolioEntity.setUrl(applyIndividualRequest.getPortfolio().get(i).getUrl());
                portfolioEntity.setProducer(producerEntity);
                portfolioRepository.save(portfolioEntity);
            }

            if (applyIndividualRequest.getDocuments() != null) {
                List<DocumentEntity> documentEntities = new ArrayList<>();
                for (UUID documentId : applyIndividualRequest.getDocuments()) {
                    DocumentEntity documentEntity =
                            documentRepository.findById(documentId).orElseThrow(() -> new ResourceNotFoundException(String.format("Document %s", documentId)));
                    if (documentEntity.getDocumentType() != DocumentType.ACCREDITATION_APPLY) {
                        throw new AccessDeniedException("Access denied");
                    }
                    documentEntities.add(documentEntity);
                }
                producerEntity.setAccreditationDocuments(documentEntities);
            }
            producerRepository.save(producerEntity);
        }
    }

    /**
     * Метод для добавления поставщика в черный список
     *
     * @param id - id поставщика
     */
    public void addToBlackList(UUID id, BlockRequest blockComment) throws ResourceNotFoundException {
        Optional<UserEntity> userEntity = userRepository.findById(id);
        if (!userEntity.isPresent() || !userEntity.get().getRole().equals(Role.PRODUCER) && !userEntity.get().getRole().equals(Role.USER)) {
            throw new ResourceNotFoundException(String.format("Producer %s", id));
        }
        ProducerEntity producer = producerRepository.findByUserId(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Producer %s", id)));
        producer.setAccreditation(AccreditationType.BLOCK);
        producer.setBlockDate(LocalDate.now());
        producer.setBlockComment(blockComment.getBlockComment());
        userEntity.get().setRole(Role.BAD);

        userRepository.save(userEntity.get());
    }

    /**
     * Метод для удаления поставщика из черного списка
     *
     * @param id - id поставщика
     */
    public void outFromBlackList(UUID id, BlockRequest blockRequest) throws ResourceNotFoundException {
        Optional<UserEntity> userEntity = userRepository.findById(id);
        if (!userEntity.isPresent() || !userEntity.get().getRole().equals(Role.BAD)) {
            throw new ResourceNotFoundException(String.format("Bad %s", id));
        }
        ProducerEntity producer = producerRepository.findByUserId(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Producer %s", id)));
        producer.setBlockComment(blockRequest.getBlockComment());
        producer.setAccreditation(AccreditationType.UNACCREDITED);
        userEntity.get().setRole(Role.USER);
        userRepository.save(userEntity.get());
    }

    // TODO: исправить дублирование кода
    private ProducerResponse convertToProducerResponse(ProducerEntity producerEntity) {
        ProducerResponse producerResponse = new ProducerResponse();

        if (producerEntity.getAccreditation().equals(AccreditationType.ACCREDITED)) {
            producerResponse.setAccreditation(AccreditationType.ACCREDITED.getLocalizedText());
        } else if (producerEntity.getUser().getRole() == Role.BAD) {
            producerResponse.setAccreditation(AccreditationType.BLOCK.getLocalizedText());
        } else {
            producerResponse.setAccreditation(AccreditationType.UNACCREDITED.getLocalizedText());
        }

        if (producerEntity.isIndividual()) {
            IndividualProducerEntity individual = physicalProducerRepository.getByProducer(producerEntity).orElseThrow(IllegalStateException::new);
            producerResponse.setFirstName(individual.getFirstName());
            producerResponse.setMiddleName(individual.getMiddleName());
            producerResponse.setLastName(individual.getLastName());
            producerResponse.setIndividual(true);
        } else {
            EntityProducerEntity entity = entityProducerRepository.getByProducer(producerEntity).orElseThrow(IllegalStateException::new);
            producerResponse.setOrganizationName(entity.getOrganizationName());
            producerResponse.setIndividual(false);
        }

        producerResponse.setProducerId(producerEntity.getUser().getId());
        producerResponse.setInn(producerEntity.getInn());
        producerResponse.setRegistrationDate(producerEntity.getRegistrationDate());
        producerResponse.setAccreditationTime(producerEntity.getAccreditationTime());
        producerResponse.setBlockDate(producerEntity.getBlockDate());
        producerResponse.setBlockComment(producerEntity.getBlockComment());
        producerResponse.setRequest(producerEntity.isRequest());
        producerResponse.setSpecialization(producerEntity.getSpecialization());
        producerResponse.setHeadcount(producerEntity.getHeadcount());
        producerResponse.setStack(getStackListFromProdEnt(producerEntity));

        return producerResponse;
    }

    private ProducerFullResponse convertToProducerFullResponse(ProducerEntity producerEntity) {
        ProducerFullResponse producerResponse = new ProducerFullResponse();

        producerResponse.setAccreditation(producerEntity.getAccreditation().getLocalizedText());
        producerResponse.setAccreditationTime(producerEntity.getAccreditationTime());
        producerResponse.setBlockDate(producerEntity.getBlockDate());

        if (producerEntity.isIndividual()) {
            IndividualProducerEntity individual = physicalProducerRepository.getByProducer(producerEntity).orElseThrow(IllegalStateException::new);
            producerResponse.setFirstName(individual.getFirstName());
            producerResponse.setMiddleName(individual.getMiddleName());
            producerResponse.setLastName(individual.getLastName());
            producerResponse.setIndividual(true);
        } else {
            EntityProducerEntity entity = entityProducerRepository.getByProducer(producerEntity).orElseThrow(IllegalStateException::new);
            producerResponse.setOrgName(entity.getOrganizationName());
            producerResponse.setIndividual(false);

            ContactPersonResponse contactPerson = new ContactPersonResponse();
            contactPerson.setFirstName(entity.getFirstName());
            contactPerson.setLastName(entity.getLastName());
            contactPerson.setMiddleName(entity.getMiddleName());
            contactPerson.setPosition(entity.getPosition());
            producerResponse.setContactPerson(contactPerson);
        }

        producerResponse.setInn(producerEntity.getInn());
        producerResponse.setId(producerEntity.getUser().getId());
        producerResponse.setRequest(producerEntity.isRequest());
        producerResponse.setRegistrationDate(producerEntity.getRegistrationDate());
        producerResponse.setAccreditationTime(producerEntity.getAccreditationTime());
        producerResponse.setBlockDate(producerEntity.getBlockDate());
        producerResponse.setBlockComment(producerEntity.getBlockComment());
        producerResponse.setBlockComment(producerEntity.getBlockComment());

        List<Long> phones = new ArrayList<>();
        for (int i = 0; i < producerEntity.getPhones().size(); i++) {
            phones.add(producerEntity.getPhones().get(i).getPhone());
        }
        producerResponse.setPhones(phones);

        producerResponse.setStacks(getStackListFromProdEnt(producerEntity));
        producerResponse.setRequest(producerEntity.isRequest());
        producerResponse.setUrl(producerEntity.getUrl());
        producerResponse.setLegalAddress(producerEntity.getLegalAddress());
        producerResponse.setActualAddress(producerEntity.getActualAddress());
        producerResponse.setHeadcount(producerEntity.getHeadcount());

        if (producerEntity.getStaffs() != null) {
            List<StaffResponse> staffs = new ArrayList<>();
            for (int i = 0; i < producerEntity.getStaffs().size(); i++) {
                StaffResponse staffResponse = new StaffResponse();
                staffResponse.setActivity(producerEntity.getStaffs().get(i).getActivity());
                staffResponse.setHeadcount(producerEntity.getStaffs().get(i).getHeadCount());
                staffs.add(staffResponse);
            }
            producerResponse.setStaff(staffs);
        }

        producerResponse.setSpecialization(producerEntity.getSpecialization());

        if (producerEntity.getPortfolios() != null) {
            List<PortfolioResponse> portfolio = new ArrayList<>();
            for (int i = 0; i < producerEntity.getPortfolios().size(); i++) {
                PortfolioResponse portfolioResponse = new PortfolioResponse();
                portfolioResponse.setCustomer(producerEntity.getPortfolios().get(i).getCustomer());
                portfolioResponse.setDescription(producerEntity.getPortfolios().get(i).getDescription());
                portfolioResponse.setUrl(producerEntity.getPortfolios().get(i).getUrl());
                portfolio.add(portfolioResponse);
            }
            producerResponse.setPortfolio(portfolio);
        }

        producerResponse.setAgencies(producerEntity.getAgencies());

        if (producerEntity.getAccreditationDocuments() != null) {
            producerResponse.setDocuments(producerEntity.getAccreditationDocuments().stream().map(DocumentEntity::getId).collect(Collectors.toList()));
        }

        return producerResponse;
    }

    /**
     * Метод получения списка технологий поставщика из сущности поставщика
     */
    private static List<String> getStackListFromProdEnt(ProducerEntity producerEntity) {
        List<String> stackList = new ArrayList<>();
        if (producerEntity.getStacks() != null) {
            for (int i = 0; i < producerEntity.getStacks().size(); i++) {
                stackList.add(producerEntity.getStacks().get(i).getTechnology());
            }
        }
        Set<String> set = new LinkedHashSet<>(stackList);   //TODO: временный костыль. Выяснить причину, почему стек дублируется
        return new ArrayList<>(set);
    }

    /**
     * DocumentRepository сеттер
     */
    @Autowired
    public void setDocumentRepository(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }
}
