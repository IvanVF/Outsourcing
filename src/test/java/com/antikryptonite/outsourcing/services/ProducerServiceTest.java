package com.antikryptonite.outsourcing.services;

import com.antikryptonite.outsourcing.controllers.enums.State;
import com.antikryptonite.outsourcing.dto.request.ApplyEntityRequest;
import com.antikryptonite.outsourcing.dto.request.BlockRequest;
import com.antikryptonite.outsourcing.dto.response.ProducerFullResponse;
import com.antikryptonite.outsourcing.dto.response.ProducersResponse;
import com.antikryptonite.outsourcing.dto.response.*;
import com.antikryptonite.outsourcing.entities.*;
import com.antikryptonite.outsourcing.exceptions.*;
import com.antikryptonite.outsourcing.mail.EmailSender;
import com.antikryptonite.outsourcing.repositories.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.*;

import java.time.*;
import java.util.*;

class ProducerServiceTest {

    private final String firstName = "Имя";

    private final String lastName = "Фамилия";

    private final String middleName = "Отчество";

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

    private final String agencies = "Информация о представительствах";

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
        //TODO: add contactPerson
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

    /**
     * Тест на проверку правильно ли метод показывает поставщика
     */
    @Test
    public void showOneProducerTest() throws ResourceNotFoundException {

        UserEntity userEntity = fillUserEntity(userId);
        ProducerEntity producerEntity = fillProducerEntity(userEntity);
        IndividualProducerEntity individualProducerEntity = fillIndividualProducerEntity(producerEntity);

        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));

        ProducerRepository producerRepository = mock(ProducerRepository.class);
        when(producerRepository.findByUserId(userId)).thenReturn(Optional.of(producerEntity));

        PhysicalProducerRepository physicalProducerRepository = mock(PhysicalProducerRepository.class);
        when(physicalProducerRepository.getByProducer(producerEntity)).thenReturn(Optional.of(individualProducerEntity));

        PhoneRepository phoneRepository = mock(PhoneRepository.class);
        PortfolioRepository portfolioRepository = mock(PortfolioRepository.class);
        StackRepository stackRepository = mock(StackRepository.class);
        StaffRepository staffRepository = mock(StaffRepository.class);

        ProducerService producerService =
                new ProducerService(userRepository, producerRepository, null, physicalProducerRepository, phoneRepository, portfolioRepository, stackRepository,
                        staffRepository
                );

        ProducerFullResponse result = producerService.showOneProducer(userId);

        assertTrue(result.isIndividual());
        assertEquals(firstName, result.getFirstName());
        assertEquals(middleName, result.getMiddleName());
        assertEquals(lastName, result.getLastName());
        assertNull(result.getOrgName());
        assertEquals(AccreditationType.ACCREDITED, result.getAccreditation());
        assertEquals(inn, result.getInn());
        assertEquals(registrationDate, result.getRegistrationDate());
        assertEquals(accreditationTime, result.getAccreditationTime());
        assertNull(result.getBlockDate());
        assertEquals(blockComment, result.getBlockComment());
        assertEquals(phonesList, result.getPhones());
        assertEquals(stackList12, result.getStacks());
        assertThat(result.isRequest()).isTrue();
        //TODO: Add contactPerson
        assertEquals(url, result.getUrl());
        assertEquals(legalAddress, result.getLegalAddress());
        assertEquals(actualAddress, result.getActualAddress());
        assertEquals(headcount, result.getHeadcount());
        assertEquals(userId, result.getId());
        assertEquals(specialisation, result.getSpecialization());
    }

    /**
     * Тест на проверку выкидывает ли метод исключение, в случае отсутствия поставщика с данным id
     */
    @Test
    public void showOneProducerExceptionTest() {

        UserRepository userRepository = mock(UserRepository.class);
        ProducerRepository producerRepository = mock(ProducerRepository.class);
        PhysicalProducerRepository physicalProducerRepository = mock(PhysicalProducerRepository.class);
        PhoneRepository phoneRepository = mock(PhoneRepository.class);
        PortfolioRepository portfolioRepository = mock(PortfolioRepository.class);
        StackRepository stackRepository = mock(StackRepository.class);
        StaffRepository staffRepository = mock(StaffRepository.class);

        ProducerService producerService =
                new ProducerService(userRepository, producerRepository, null, physicalProducerRepository, phoneRepository, portfolioRepository, stackRepository,
                        staffRepository
                );

        assertThrows(ResourceNotFoundException.class, () -> producerService.showOneProducer(userId));
    }

    /**
     * Тест на проверку правильно ли метод показывает всех поставщиков
     */
    @Test
    public void showProducersTest() {

        UserEntity userEntity = fillUserEntity(userId);
        UserEntity userEntity2 = fillUserEntity(userId2);
        UserEntity userEntity3 = fillUserEntity(userId3);

        ProducerEntity producerEntity = fillProducerEntity(userEntity);
        ProducerEntity producerEntity2 = fillProducerEntity(userEntity2);
        producerEntity2.setStacks(createStackEntityList(stack3, stack4));
        producerEntity2.setInn(inn2.toString());
        ProducerEntity producerEntity3 = fillProducerEntity(userEntity3);
        producerEntity3.setStacks(createStackEntityList(stack1, stack4));
        producerEntity3.setInn(inn3.toString());

        IndividualProducerEntity individualProducerEntity = fillIndividualProducerEntity(producerEntity);
        IndividualProducerEntity individualProducerEntity2 = fillIndividualProducerEntity(producerEntity2);
        IndividualProducerEntity individualProducerEntity3 = fillIndividualProducerEntity(producerEntity3);

        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));

        ProducerRepository producerRepository = mock(ProducerRepository.class);
        when(producerRepository.findByUserId(userId)).thenReturn(Optional.of(producerEntity));
        List<ProducerEntity> producerEntityList = new ArrayList<>();
        producerEntityList.add(producerEntity);
        producerEntityList.add(producerEntity2);
        producerEntityList.add(producerEntity3);

        when(producerRepository.getAccreditedProducers()).thenReturn(producerEntityList);

        PhysicalProducerRepository physicalProducerRepository = mock(PhysicalProducerRepository.class);
        when(physicalProducerRepository.getByProducer(producerEntity)).thenReturn(Optional.of(individualProducerEntity));
        when(physicalProducerRepository.getByProducer(producerEntity2)).thenReturn(Optional.of(individualProducerEntity2));
        when(physicalProducerRepository.getByProducer(producerEntity3)).thenReturn(Optional.of(individualProducerEntity3));

        PhoneRepository phoneRepository = mock(PhoneRepository.class);
        PortfolioRepository portfolioRepository = mock(PortfolioRepository.class);
        StackRepository stackRepository = mock(StackRepository.class);
        StaffRepository staffRepository = mock(StaffRepository.class);

        ProducerService producerService = new ProducerService(userRepository, producerRepository, null, physicalProducerRepository, phoneRepository, portfolioRepository, stackRepository, staffRepository);

        ProducersResponse resultForAllProducers = producerService.showProducers(State.ACCREDITED, stack1);
        assertThat(resultForAllProducers.getProducersList().get(0).getStack()).isEqualTo(stackList12);
        assertThat(resultForAllProducers.getProducersList().get(1).getStack()).isEqualTo(stackList14);

    }

    /**
     * Тест на проверку аккредитации поставщика
     */
    @Test
    public void accreditProducerTest() throws ResourceNotFoundException {

        UserEntity userEntity = fillUserEntity(userId);
        ProducerEntity producerEntity = fillProducerEntity(userEntity);

        producerEntity.setAccreditation(AccreditationType.UNACCREDITED);
        producerEntity.setAccreditationTime(null);

        IndividualProducerEntity individualProducerEntity = fillIndividualProducerEntity(producerEntity);

        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));

        ProducerRepository producerRepository = mock(ProducerRepository.class);
        when(producerRepository.findByUserId(userId)).thenReturn(Optional.of(producerEntity));

        PhysicalProducerRepository physicalProducerRepository = mock(PhysicalProducerRepository.class);
        when(physicalProducerRepository.getByProducer(producerEntity)).thenReturn(Optional.of(individualProducerEntity));

        PhoneRepository phoneRepository = mock(PhoneRepository.class);
        PortfolioRepository portfolioRepository = mock(PortfolioRepository.class);
        StackRepository stackRepository = mock(StackRepository.class);
        StaffRepository staffRepository = mock(StaffRepository.class);

        ProducerService producerService =
                new ProducerService(userRepository, producerRepository, null, physicalProducerRepository, phoneRepository, portfolioRepository, stackRepository,
                        staffRepository
                );

//        EmailSender emailSender = Mockito.mock(EmailSender.class);
//        producerService.setEmailSender(emailSender);

        producerService.accreditProducer(userId);

        assertThat(producerEntity.getAccreditation()).isEqualTo(AccreditationType.ACCREDITED);
        assertThat(producerEntity.getAccreditationTime()).isEqualToIgnoringHours(LocalDateTime.now());
        assertThat(producerEntity.getUser().getRole()).isEqualTo(Role.PRODUCER);
    }

    /**
     * Тест для проверки метода подачи запроса на аккредитацию поставщикка
     */
    @Test
    public void callForProducerAccreditationTest() throws ApplicationException {
        UserEntity userEntity = fillUserEntity(userId);
        ProducerEntity producerEntity = fillProducerEntity(userEntity);
        producerEntity.setAccreditation(AccreditationType.UNACCREDITED);
        producerEntity.setAccreditationTime(null);
        producerEntity.setRequest(false);

        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));

        ProducerRepository producerRepository = mock(ProducerRepository.class);
        when(producerRepository.findByUserId(userId)).thenReturn(Optional.of(producerEntity));

        ApplyEntityRequest applyEntityRequest = new ApplyEntityRequest();
        applyEntityRequest.setActualAddress(actualAddress);
        applyEntityRequest.setLegalAddress(legalAddress);
        applyEntityRequest.setAgencies(agencies);
        applyEntityRequest.setHeadcount(15);
        applyEntityRequest.setPhone(phonesList.get(0));
        applyEntityRequest.setPortfolio(createPortfolioResponseList());
        applyEntityRequest.setSpecialization(specialisation);
        applyEntityRequest.setStack(stackList12);
        applyEntityRequest.setStaff(createStaffResponseList());
        applyEntityRequest.setUrl(url);

        PhoneRepository phoneRepository = mock(PhoneRepository.class);
        PortfolioRepository portfolioRepository = mock(PortfolioRepository.class);
        StackRepository stackRepository = mock(StackRepository.class);
        StaffRepository staffRepository = mock(StaffRepository.class);

        ProducerService producerService =
                new ProducerService(userRepository, producerRepository, null, null, phoneRepository, portfolioRepository, stackRepository, staffRepository);
        producerService.callForEntityProducerAccreditation(userId, applyEntityRequest);

        ArgumentCaptor<ProducerEntity> captorProducerEntity = ArgumentCaptor.forClass(ProducerEntity.class);
        verify(producerRepository).save(captorProducerEntity.capture());
        ProducerEntity argumentProducerEntity = captorProducerEntity.getValue();
        assertThat(argumentProducerEntity).isEqualTo(producerEntity);

        Mockito.verify(phoneRepository, Mockito.atLeast(1)).save(Mockito.any());
        Mockito.verify(staffRepository, Mockito.atLeast(1)).save(Mockito.any());
        Mockito.verify(stackRepository, Mockito.atLeast(1)).save(Mockito.any());
        Mockito.verify(portfolioRepository, Mockito.atLeast(1)).save(Mockito.any());


    }

    /**
     * Тест на проверку ошибки, вызванной отсутствием поставщика, подающего заявку на аккредитацию
     */
    @Test
    public void callForProducerAccreditationExceptionTest() throws ResourceNotFoundException {

        UserRepository userRepository = mock(UserRepository.class);
        ProducerRepository producerRepository = mock(ProducerRepository.class);

        ApplyEntityRequest applyEntityRequest = new ApplyEntityRequest();

        PhoneRepository phoneRepository = mock(PhoneRepository.class);
        PortfolioRepository portfolioRepository = mock(PortfolioRepository.class);
        StackRepository stackRepository = mock(StackRepository.class);
        StaffRepository staffRepository = mock(StaffRepository.class);

        ProducerService producerService =
                new ProducerService(userRepository, producerRepository, null, null, phoneRepository, portfolioRepository, stackRepository, staffRepository);

        assertThrows(ResourceNotFoundException.class, () -> producerService.callForEntityProducerAccreditation(userId, applyEntityRequest));
    }

    /**
     * Тест для проверки метода добавления поставщика в ЧС
     */
    @Test
    public void addToBlackListTest() throws ResourceNotFoundException {

        UserEntity userEntity = fillUserEntity(userId);
        userEntity.setRole(Role.PRODUCER);
        ProducerEntity producerEntity = fillProducerEntity(userEntity);
        producerEntity.setBlockDate(null);

        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));

        ProducerRepository producerRepository = mock(ProducerRepository.class);
        when(producerRepository.findByUserId(userId)).thenReturn(Optional.of(producerEntity));

        PhoneRepository phoneRepository = mock(PhoneRepository.class);
        PortfolioRepository portfolioRepository = mock(PortfolioRepository.class);
        StackRepository stackRepository = mock(StackRepository.class);
        StaffRepository staffRepository = mock(StaffRepository.class);

        ProducerService producerService =
                new ProducerService(userRepository, producerRepository, null, null, phoneRepository, portfolioRepository, stackRepository, staffRepository);

//        EmailSender emailSender = Mockito.mock(EmailSender.class);
//        producerService.setEmailSender(emailSender);

        BlockRequest blockRequest = new BlockRequest();
        blockRequest.setBlockComment("такой ты плохой");

        producerService.addToBlackList(userId, blockRequest);

        assertThat(userEntity.getRole()).isEqualTo(Role.BAD);
        assertThat(producerEntity.getBlockDate()).isEqualTo(LocalDate.now());
    }

    /**
     * Тест на проверку ошибки, вызванной отсутствием поставщика, добавляемого в ЧС
     */
    @Test
    public void addToBlackListExceptionTest() {

        UserRepository userRepository = mock(UserRepository.class);
        ProducerRepository producerRepository = mock(ProducerRepository.class);

        PhoneRepository phoneRepository = mock(PhoneRepository.class);
        PortfolioRepository portfolioRepository = mock(PortfolioRepository.class);
        StackRepository stackRepository = mock(StackRepository.class);
        StaffRepository staffRepository = mock(StaffRepository.class);

        BlockRequest blockRequest = new BlockRequest();
        blockRequest.setBlockComment("такой ты плохой");

        ProducerService producerService =
                new ProducerService(userRepository, producerRepository, null, null, phoneRepository, portfolioRepository, stackRepository, staffRepository);

        assertThrows(ResourceNotFoundException.class, () -> producerService.addToBlackList(userId, blockRequest));
    }

    /**
     * Тест для проверки метода удаления поставщика из ЧС
     */
    @Test
    public void outFromBlackListTest() throws ResourceNotFoundException {

        UserEntity userEntity = fillUserEntity(userId);
        userEntity.setRole(Role.BAD);
        ProducerEntity producerEntity = fillProducerEntity(userEntity);
        producerEntity.setBlockDate(LocalDate.now());

        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));

        ProducerRepository producerRepository = mock(ProducerRepository.class);
        when(producerRepository.findByUserId(userId)).thenReturn(Optional.of(producerEntity));

        PhoneRepository phoneRepository = mock(PhoneRepository.class);
        PortfolioRepository portfolioRepository = mock(PortfolioRepository.class);
        StackRepository stackRepository = mock(StackRepository.class);
        StaffRepository staffRepository = mock(StaffRepository.class);

        ProducerService producerService =
                new ProducerService(userRepository, producerRepository, null, null, phoneRepository, portfolioRepository, stackRepository, staffRepository);

//        EmailSender emailSender = Mockito.mock(EmailSender.class);
//        producerService.setEmailSender(emailSender);

        BlockRequest blockRequest = new BlockRequest();
        blockRequest.setBlockComment("Не такой ты и плохой");

        producerService.outFromBlackList(userId, blockRequest);

        assertThat(userEntity.getRole()).isEqualTo(Role.USER);
        assertThat(producerEntity.getAccreditation()).isEqualTo(AccreditationType.UNACCREDITED);
    }

    /**
     * Тест на проверку ошибки, вызванной отсутствием поставщика, возвращаемого из ЧС
     */
    @Test
    public void outFromBlackListExceptionTest() {

        UserRepository userRepository = mock(UserRepository.class);
        ProducerRepository producerRepository = mock(ProducerRepository.class);

        PhoneRepository phoneRepository = mock(PhoneRepository.class);
        PortfolioRepository portfolioRepository = mock(PortfolioRepository.class);
        StackRepository stackRepository = mock(StackRepository.class);
        StaffRepository staffRepository = mock(StaffRepository.class);

        BlockRequest blockRequest = new BlockRequest();
        blockRequest.setBlockComment("Не такой ты и плохой");

        ProducerService producerService =
                new ProducerService(userRepository, producerRepository, null, null, phoneRepository, portfolioRepository, stackRepository, staffRepository);

        assertThrows(ResourceNotFoundException.class, () -> producerService.outFromBlackList(userId, blockRequest));
    }
}
