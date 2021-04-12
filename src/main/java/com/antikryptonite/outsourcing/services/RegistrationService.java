package com.antikryptonite.outsourcing.services;

import com.antikryptonite.outsourcing.dto.request.*;
import com.antikryptonite.outsourcing.entities.*;
import com.antikryptonite.outsourcing.exceptions.*;
import com.antikryptonite.outsourcing.mail.*;
import com.antikryptonite.outsourcing.repositories.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

/**
 * Сервис регистрации поставщиков
 */
@Service
@Transactional
public class RegistrationService {

    private final UserRepository userRepository;

    private final ProducerRepository producerRepository;

    private final EntityProducerRepository entityProducerRepository;

    private final PhysicalProducerRepository indProducerRepository;

    private PhoneRepository phoneRepository;

    private EmailSender emailSender;

    private PasswordEncoder passwordEncoder;

    @Value("${front.url}")
    private String frontUrl;

    /**
     * Конструктор
     */
    @Autowired
    public RegistrationService(
            UserRepository userRepository, ProducerRepository producerRepository,
            EntityProducerRepository entityProducerRepository,
            PhysicalProducerRepository indProducerRepository
    ) {
        this.userRepository = userRepository;
        this.producerRepository = producerRepository;
        this.entityProducerRepository = entityProducerRepository;
        this.indProducerRepository = indProducerRepository;
    }

    /**
     * Сохранение профиля физического лица, присвоив ему роль по умолчанию
     *
     * @param registrationRequest - параметры пользователя
     */
    public void saveIndProducer(RegistrationIndividualRequest registrationRequest) throws UniqueException {
        ProducerEntity producerEntity = fillProducer(registrationRequest.getLogin(), registrationRequest.getPassword(), registrationRequest.getInnNumber(),
                registrationRequest.getPhoneNumber()
        );

        producerEntity.setIndividual(true);
        IndividualProducerEntity physicalProducer = new IndividualProducerEntity();
        physicalProducer.setId(UUID.randomUUID());
        physicalProducer.setProducer(producerEntity);
        physicalProducer.setFirstName(registrationRequest.getFirstName());
        physicalProducer.setLastName(registrationRequest.getLastName());
        physicalProducer.setMiddleName(registrationRequest.getMiddleName());

        userRepository.save(producerEntity.getUser());
        producerRepository.save(producerEntity);
        indProducerRepository.save(physicalProducer);
        if (producerEntity.getPhones() != null && !producerEntity.getPhones().isEmpty()) {
            phoneRepository.saveAll(producerEntity.getPhones());
        }
        sendConfirmEmail(producerEntity);
    }

    /**
     * Сохранение профиля юридического лица, присвоив ему роль по умолчанию
     *
     * @param registrationRequest - параметры пользователя
     */
    public void saveEntProducer(RegistrationEntityRequest registrationRequest) throws UniqueException {
        ProducerEntity producerEntity = fillProducer(registrationRequest.getLogin(), registrationRequest.getPassword(), registrationRequest.getInnNumber(),
                registrationRequest.getPhoneNumber()
        );
        producerEntity.setIndividual(false);
        EntityProducerEntity entityProducer = new EntityProducerEntity();
        entityProducer.setId(UUID.randomUUID());
        entityProducer.setProducer(producerEntity);
        entityProducer.setOrganizationName(registrationRequest.getOrgName());

        userRepository.save(producerEntity.getUser());
        producerRepository.save(producerEntity);
        entityProducerRepository.save(entityProducer);
        if (producerEntity.getPhones() != null && !producerEntity.getPhones().isEmpty()) {
            phoneRepository.saveAll(producerEntity.getPhones());
        }

        sendConfirmEmail(producerEntity);
    }

    private ProducerEntity fillProducer(String login, String password, String inn, Long phone) throws UniqueException {
        if (userRepository.findByLogin(login).isPresent()) {
            throw new UniqueException("User with this email already exists");
        }
        if (phone != null) {
            if (phoneRepository.existsByPhone(phone)) {
                throw new UniqueException("User with this phone number already exists");
            }
        }
        if (producerRepository.findByInn(inn).isPresent()) {
            throw new UniqueException("User with this INN number already exists");
        }

        UserEntity u = new UserEntity();
        u.setLogin(login);
        u.setId(UUID.randomUUID());
        u.setPassword(passwordEncoder.encode(password));
        u.setRole(Role.USER);

        ProducerEntity producerEntity = new ProducerEntity();

        if (phone != null) {
            PhoneEntity phoneEntity = new PhoneEntity();
            phoneEntity.setPhone(phone);
            phoneEntity.setProducer(producerEntity);
            phoneEntity.setId(UUID.randomUUID());
            producerEntity.setPhones(Collections.singletonList(phoneEntity));
        }

        producerEntity.setInn(inn);
        producerEntity.setConfirm(false);
        producerEntity.setConfirmCode(UUID.randomUUID());
        producerEntity.setProducerId(UUID.randomUUID());
        producerEntity.setUser(u);
        producerEntity.setAccreditation(AccreditationType.UNACCREDITED);
        producerEntity.setRequest(false);
        producerEntity.setRegistrationDate(LocalDate.now());
        return producerEntity;
    }

    private void sendConfirmEmail(ProducerEntity producerEntity) {
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setEmail(producerEntity.getUser().getLogin());
        emailMessage.setText(
                "Добро пожаловать в систему “ЭТП“, для завершения регистрации перейдите по ссылке: " + frontUrl + "/confirm/" +
                        producerEntity.getConfirmCode());
        emailMessage.setSubject("Anti-Kryptonite - регистрация");
        emailSender.send(emailMessage);
    }

    /**
     * Активация пользователя
     *
     * @param confirmRegistration - личный номер пользователя
     */
    public void activateProducer(UUID confirmRegistration) throws RegistrationFallsException {
        ProducerEntity producer = producerRepository.findByConfirmCode(confirmRegistration).orElseThrow(RegistrationFallsException::new);
        producer.setConfirm(true);
        producer.setConfirmCode(null);
        producer.setTelegramToken(UUID.randomUUID());
        producerRepository.save(producer);
    }

    /**
     * Смена пароля пользователем
     * @param id - id пользователя
     * @param request - запрос с новым паролем
     */
    public void changePassword(UUID id, ChangePasswordRequest request) throws ResourceNotFoundException, ValidationFallsException {
        UserEntity userEntity = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("User %s", id)));
        if (passwordEncoder.matches(request.getPasswordOld(), userEntity.getPassword())) {
            userEntity.setPassword(passwordEncoder.encode(request.getPasswordNew()));
            userRepository.save(userEntity);
        } else {
            throw new ValidationFallsException("The current password is incorrect");
        }
    }

    /**
     * EmailSender сеттер
     */
    @Autowired
    public void setEmailSender(@Lazy EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    /**
     * PasswordEncoder сеттер
     */
    @Autowired
    public void setPasswordEncoder(@Lazy PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * phoneRepository сеттер
     */
    @Autowired
    public void setPhoneRepository(PhoneRepository phoneRepository) {
        this.phoneRepository = phoneRepository;
    }



}
