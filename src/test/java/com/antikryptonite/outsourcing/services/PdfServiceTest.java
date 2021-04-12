package com.antikryptonite.outsourcing.services;

import com.antikryptonite.outsourcing.dto.response.PortfolioResponse;
import com.antikryptonite.outsourcing.dto.response.StaffResponse;
import com.antikryptonite.outsourcing.entities.*;
import com.antikryptonite.outsourcing.exceptions.ResourceNotFoundException;
import com.antikryptonite.outsourcing.repositories.DocumentRepository;
import com.antikryptonite.outsourcing.repositories.EntityProducerRepository;
import com.antikryptonite.outsourcing.repositories.PhysicalProducerRepository;
import com.antikryptonite.outsourcing.repositories.ProducerRepository;
import com.itextpdf.text.DocumentException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.when;

public class PdfServiceTest {

    private final String firstName = "Рабыня";

    private final String lastName = "Изаура";

    private final String middleName = "Петрович";

    private final String position = "Наливатель кофе";

    private final String orgName = "ООО Зеленоглазое такси";

    private final Long inn = 12345L;
    private final Long inn2 = 123466L;
    private final Long inn3 = 1234777L;

    private final LocalDateTime accreditationTime = LocalDateTime.now();

    private final LocalDate registrationDate = LocalDate.now();

    private final String blockComment = "Был плохой, стал хороший";

    private final long phone1 = 8005553535L;

    private final long phone2 = 8005553536L;

    private final String stack1 = "Java";

    private final String stack2 = "PHP";

    private final String stack3 = ".Net";

    private final String stack4 = "C++";

    private final String url = "company.com";

    private final String legalAddress = "Ул. Пушкина, 123";

    private final String actualAddress = "Ул. Ленина, 321";

    private final int headcount = 15;

    private final String specialisation = "Мобильные приложения";

    private final String customer1 = "ООО Компания";

    private final String customer2 = "ИП Медведев";

    private final String description1 = "Делали сайт";

    private final String description2 = "Делали ещё сайт";

    private final String siteUrl1 = "company.com";

    private final String siteUrl2 = "othercompany.com";

    private final String agencies = "ОАО Рога и копыта";

    private final UUID userId = UUID.randomUUID();
    private final UUID userId2 = UUID.randomUUID();
    private final UUID userId3 = UUID.randomUUID();

    private List<Long> phonesList = Arrays.asList(phone1, phone2);

    private List<String> stackList12 = Arrays.asList(stack1, stack2);
    private List<String> stackList14 = Arrays.asList(stack1, stack4);

    private List<StackEntity> createStackEntityList(String firstStack, String secondStack) {

        StackEntity stackEntity1 = new StackEntity();
        StackEntity stackEntity2 = new StackEntity();
        stackEntity1.setTechnology(firstStack);
        stackEntity2.setTechnology(secondStack);
        List<StackEntity> stackList = Arrays.asList(stackEntity1, stackEntity2);
        return stackList;
    }

    private List<PhoneEntity> createPhoneEntityList() {

        PhoneEntity phoneEntity1 = new PhoneEntity();
        phoneEntity1.setPhone(phone1);
        PhoneEntity phoneEntity2 = new PhoneEntity();
        phoneEntity2.setPhone(phone2);
        List<PhoneEntity> phoneEntityList = Arrays.asList(phoneEntity1, phoneEntity2);
        return phoneEntityList;
    }

    private List<StaffEntity> createStaffEntityList() {

        StaffEntity staffEntity1 = new StaffEntity();
        StaffEntity staffEntity2 = new StaffEntity();
        StaffEntity staffEntity3 = new StaffEntity();
        staffEntity1.setActivity("Back-end");
        staffEntity1.setHeadCount(3);
        staffEntity2.setActivity("Front-end");
        staffEntity2.setHeadCount(1);
        staffEntity3.setActivity("QA");
        staffEntity3.setHeadCount(1);
        List<StaffEntity> staffEntityList = Arrays.asList(staffEntity1, staffEntity2, staffEntity3);
        return staffEntityList;
    }

    private List<StaffResponse> createStaffResponseList() {
        StaffResponse staffResponse = new StaffResponse();
        staffResponse.setActivity("Back-end");
        staffResponse.setHeadcount(3);
        List<StaffResponse> staffResponseList = new ArrayList<>();
        staffResponseList.add(staffResponse);
        return staffResponseList;
    }

    private List<PortfolioEntity> createPortfolioEntityList() {

        PortfolioEntity portfolioEntity1 = new PortfolioEntity();
        PortfolioEntity portfolioEntity2 = new PortfolioEntity();
        portfolioEntity1.setCustomer(customer1);
        portfolioEntity1.setDescription(description1);
        portfolioEntity1.setUrl(siteUrl1);
        portfolioEntity2.setCustomer(customer2);
        portfolioEntity2.setDescription(description2);
        portfolioEntity2.setUrl(siteUrl2);
        List<PortfolioEntity> portfolioEntityList = Arrays.asList(portfolioEntity1, portfolioEntity2);
        return portfolioEntityList;
    }

    private List<PortfolioResponse> createPortfolioResponseList() {
        PortfolioResponse portfolioResponse = new PortfolioResponse();
        portfolioResponse.setUrl(url);
        portfolioResponse.setDescription(description1);
        portfolioResponse.setCustomer(customer1);
        List<PortfolioResponse> portfolioResponseList = new ArrayList<>();
        portfolioResponseList.add(portfolioResponse);
        return portfolioResponseList;
    }

    private UserEntity fillUserEntity(UUID id) {

        UserEntity userEntity = new UserEntity();
        userEntity.setId(id);
        userEntity.setRole(Role.PRODUCER);
        return userEntity;
    }

    private ProducerEntity fillProducerEntity(UserEntity userEntity) {

        ProducerEntity producerEntity = new ProducerEntity();
        producerEntity.setUser(userEntity);
        producerEntity.setIndividual(true);
        producerEntity.setAccreditation(AccreditationType.ACCREDITED);
        producerEntity.setInn(inn.toString());
        producerEntity.setAccreditationTime(accreditationTime);
        producerEntity.setRegistrationDate(registrationDate);
        producerEntity.setBlockComment(blockComment);
        producerEntity.setPhones(createPhoneEntityList());
        producerEntity.setRequest(true);
        producerEntity.setUrl(url);
        producerEntity.setLegalAddress(legalAddress);
        producerEntity.setActualAddress(actualAddress);
        producerEntity.setHeadcount(headcount);
        producerEntity.setStaffs(createStaffEntityList());
        producerEntity.setSpecialization(specialisation);
        producerEntity.setStacks(createStackEntityList(stack1, stack2));
        producerEntity.setPortfolios(createPortfolioEntityList());
        producerEntity.setAgencies(agencies);
        return producerEntity;
    }

    private IndividualProducerEntity fillIndividualProducerEntity(ProducerEntity producerEntity) {

        IndividualProducerEntity individualProducerEntity = new IndividualProducerEntity();
        individualProducerEntity.setProducer(producerEntity);
        individualProducerEntity.setFirstName(firstName);
        individualProducerEntity.setMiddleName(middleName);
        individualProducerEntity.setLastName(lastName);
        return individualProducerEntity;
    }

    private EntityProducerEntity fillEntityProducerEntity(ProducerEntity producerEntity) {
        EntityProducerEntity entityProducerEntity = new EntityProducerEntity();
        entityProducerEntity.setProducer(producerEntity);
        entityProducerEntity.setPosition(position);
        entityProducerEntity.setFirstName(firstName);
        entityProducerEntity.setMiddleName(middleName);
        entityProducerEntity.setLastName(lastName);
        entityProducerEntity.setOrganizationName(orgName);
        return entityProducerEntity;
    }


    @Test
    public void createPdfTest() throws IOException, DocumentException, ResourceNotFoundException {

        UserEntity userEntity = fillUserEntity(userId);
        ProducerEntity producerEntity = fillProducerEntity(userEntity);
        producerEntity.setIndividual(false);
        EntityProducerEntity entityProducerEntity = fillEntityProducerEntity(producerEntity);
        IndividualProducerEntity individualProducerEntity = fillIndividualProducerEntity(producerEntity);

        EntityProducerRepository entityProducerRepository = mock(EntityProducerRepository.class);
        when(entityProducerRepository.getByProducer(Mockito.any())).thenReturn(Optional.of(entityProducerEntity));

        PhysicalProducerRepository physicalProducerRepository = mock(PhysicalProducerRepository.class);
        when(physicalProducerRepository.getByProducer(Mockito.any())).thenReturn(Optional.of(individualProducerEntity));

        ProducerRepository producerRepository = mock(ProducerRepository.class);
        when(producerRepository.findByUserId(Mockito.any())).thenReturn(Optional.of(producerEntity));

        DocumentRepository documentRepository = mock(DocumentRepository.class);

        PdfService pdfService = new PdfService(entityProducerRepository, physicalProducerRepository, producerRepository, documentRepository);

        pdfService.createPdf(userId);
    }

}
