package com.antikryptonite.outsourcing.telegram;

import com.antikryptonite.outsourcing.dto.request.*;
import com.antikryptonite.outsourcing.dto.response.*;
import com.antikryptonite.outsourcing.entities.*;
import com.antikryptonite.outsourcing.exceptions.ResourceNotFoundException;
import com.antikryptonite.outsourcing.mail.*;
import com.antikryptonite.outsourcing.repositories.*;
import lombok.extern.java.Log;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.*;
import java.util.*;

/**
 * Аспект для отправки уведомлений по телеграмм
 */
@Component
@Aspect
@Log
public class NotificationAspect {

    private EmailSender emailSender;

    private final TelegramSender telegramSender;

    private final ProducerRepository producerRepository;

    private final PurchaseRepository purchaseRepository;

    private final TaskScheduler taskScheduler;

    @Value("${front.url}")
    private String frontUrl;

    /**
     * Констуктор
     */
    @Autowired
    public NotificationAspect(
            TelegramSender telegramSender, ProducerRepository producerRepository, PurchaseRepository purchaseRepository,
            TaskScheduler taskScheduler
    ) {
        this.telegramSender = telegramSender;
        this.producerRepository = producerRepository;
        this.purchaseRepository = purchaseRepository;
        this.taskScheduler = taskScheduler;
    }

    /**
     * EmailSender сеттер
     */
    @Autowired
    public void setEmailSender(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    /**
     * Уведомление о добавлении в ЧС
     */
    @AfterReturning(pointcut = "execution(public void com.antikryptonite.outsourcing.services.ProducerService.addToBlackList(..))")
    public void inBlackList(JoinPoint joinPoint) {
        UUID userId = (UUID)joinPoint.getArgs()[0];

        ProducerEntity producerEntity = getProducerEntity(userId);
        if (producerEntity == null) {
            return;
        }
        telegramSender.sendMessage(producerEntity, "Ваша учётная запись заблокирована.");

        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setEmail(producerEntity.getUser().getLogin());
        emailMessage.setSubject("Anti-Kryptonite - черный список");
        emailMessage.setText("Ваша учётная запись заблокирована.");
        emailSender.send(emailMessage);
    }

    /**
     * Уведомление об исключении из ЧС
     */
    @AfterReturning(pointcut = "execution(public void com.antikryptonite.outsourcing.services.ProducerService.outFromBlackList(..))")
    public void outBlackList(JoinPoint joinPoint) {
        UUID userId = (UUID)joinPoint.getArgs()[0];

        ProducerEntity producerEntity = getProducerEntity(userId);
        if (producerEntity == null) {
            return;
        }
        telegramSender.sendMessage(producerEntity, "Ваша учётная запись разблокирована.");

        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setEmail(producerEntity.getUser().getLogin());
        emailMessage.setSubject("Anti-Kryptonite - выход из черного списка");
        emailMessage.setText("Вы были исключены из черного списка компании.");
        emailSender.send(emailMessage);
    }

    /**
     * Уведомление об отправлении заявки на аккредитацию юридического лица
     */
    @AfterReturning(pointcut = "execution(public void com.antikryptonite.outsourcing.services.ProducerService.callForEntityProducerAccreditation(..))")
    public void callForAccreditationEntity(JoinPoint joinPoint) {
        UUID userId = (UUID)joinPoint.getArgs()[0];

        ProducerEntity producerEntity = getProducerEntity(userId);
        if (producerEntity == null) {
            return;
        }
        telegramSender.sendMessage(producerEntity, "Ваша заявка на аккредитацию отправлена.");
    }

    /**
     * Уведомление об отправлении заявки на аккредитацию физического лица
     */
    @AfterReturning(pointcut = "execution(public void com.antikryptonite.outsourcing.services.ProducerService.callForProducerIndividualAccreditation(..))")
    public void callForAccreditationIndividual(JoinPoint joinPoint) {
        UUID userId = (UUID)joinPoint.getArgs()[0];

        ProducerEntity producerEntity = getProducerEntity(userId);
        if (producerEntity == null) {
            return;
        }
        telegramSender.sendMessage(producerEntity, "Ваша заявка на аккредитацию отправлена.");
    }

    /**
     * Уведомление об аккредитации
     */
    @AfterReturning(pointcut = "execution(public void com.antikryptonite.outsourcing.services.ProducerService.accreditProducer(..))")
    public void accreditation(JoinPoint joinPoint) {
        UUID userId = (UUID)joinPoint.getArgs()[0];

        ProducerEntity producerEntity = getProducerEntity(userId);
        if (producerEntity == null) {
            return;
        }
        telegramSender.sendMessage(producerEntity, "Ваша заявка на аккредитацию принята. Вашей учетной записи присвоен статус ”Аккредитован”. " +
                "Теперь Вам доступна возможность подать заявки на участие в закупках. Список закупок: " + frontUrl + "/purchases");

        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setEmail(producerEntity.getUser().getLogin());
        emailMessage.setSubject("Anti-Kryptonite - аккредитация");
        emailMessage.setText("Ваша заявка на аккредитацию принята. Вашей учетной записи присвоен статус ”Аккредитован”. " +
                "Теперь Вам доступна возможность подать заявки на участие в закупках. Список закупок: " + frontUrl + "/purchases");
        emailSender.send(emailMessage);
    }

    /**
     * Уведомление о публикации закупки
     */
    @AfterReturning(pointcut = "execution(public com.antikryptonite.outsourcing.dto.response.PurchaseCreateResponse " +
            "com.antikryptonite.outsourcing.services.PurchaseService.createPurchase(..))", returning = "returnValue")
    public void publishPurchase(Object returnValue) {
        if (returnValue == null) {
            return;
        }
        PurchaseCreateResponse response = (PurchaseCreateResponse)returnValue;
        PurchaseEntity entity = getPurchaseEntity(response.getPurchaseId());
        producerRepository.getOverallProducers().forEach(
                producer -> telegramSender.sendMessage(producer, String.format("Открыта закупка \"%s\" #%d. Закупка: "
                        + frontUrl + "/purchases/" + entity.getPurchaseId(), entity.getName(), entity.getNumber())));
    }

    /**
     * Уведомление об отправке уведомлений
     */
    @AfterReturning(pointcut = "execution(public com.antikryptonite.outsourcing.dto.response.ApplicationResponse " +
            "com.antikryptonite.outsourcing.services.ApplicationService.createApplication(..))",
            returning = "returnValue")
    public void sendApplication(JoinPoint joinPoint, Object returnValue) throws ResourceNotFoundException {
        if (returnValue == null) {
            return;
        }
        ApplicationResponse response = (ApplicationResponse)returnValue;
        ProducerEntity producer = producerRepository.findById(response.getProducerId()).orElseThrow(
                () -> new ResourceNotFoundException(String.format("Producer %s", response.getProducerId())));
        if (producer == null) {
            return;
        }
        UUID purchaseId = (UUID)joinPoint.getArgs()[1];

        PurchaseEntity purchase = getPurchaseEntity(purchaseId);
        if (purchase == null) {
            return;
        }
        telegramSender.sendMessage(
                producer, String.format("Ваша заявка на участие в закупке \"%s\" #%d отправлена. Закупка: "
                        + frontUrl + "/purchases/" + purchase.getPurchaseId(), purchase.getName(), purchase.getNumber()));

        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setEmail(producer.getUser().getLogin());
        emailMessage.setSubject("Заявка на участие в закупке");
        emailMessage.setText(String.format("Ваша заявка на участие в закупке \"%s\" %d отправлена. Закупка: "
                + frontUrl + "/purchases/" + purchase.getPurchaseId(), purchase.getName(), purchase.getNumber()));
        emailSender.send(emailMessage);
    }

//    @AfterReturning(pointcut = "execution(public void com.antikryptonite.outsourcing.services.PurchaseService.createPurchase(..))")
//    public void openPurchase() {
//        List<PurchaseEntity> purchaseEntities = purchaseRepository.findAll();
//        for (PurchaseEntity purchase: purchaseEntities) {
//            ZoneId zoneId = ZoneId.systemDefault();
//            LocalDate localDate = purchase.getStartDate();
//            Date date = Date.from(localDate.atStartOfDay(zoneId).toInstant());
//            taskScheduler.schedule(sendMessageEveryone(purchase), date);
//        }
//    }
    /**
     * * Уведомление всех поставщиков об открытии приема заявок
     */
    @AfterReturning(pointcut = "execution(public com.antikryptonite.outsourcing.dto.response.PurchaseCreateResponse " +
            "com.antikryptonite.outsourcing.services.PurchaseService.createPurchase(..))", returning = "returnValue")
    public void openPurchase(Object returnValue) throws ResourceNotFoundException {
        PurchaseCreateResponse response = (PurchaseCreateResponse)returnValue;
        PurchaseEntity purchase = purchaseRepository.findById(response.getPurchaseId()).orElseThrow(
                () -> new ResourceNotFoundException(String.format("Purchase %s", response.getPurchaseId())));
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate localDate = purchase.getStartDate();
        Date date = Date.from(localDate.atStartOfDay(zoneId).toInstant());
        taskScheduler.schedule(sendMessageEveryone(purchase), date);
    }

    /**
     * Уведомление всех поставщиков с предупреждением о закрытии приема заявок на закупку через 3 дня
     */
    @AfterReturning(pointcut = "execution(public void com.antikryptonite.outsourcing.services.PurchaseService.createPurchase(..))")
    public void threeDayForClose() {
        List<PurchaseEntity> purchaseEntities = purchaseRepository.findAll();
        for (PurchaseEntity purchase: purchaseEntities) {
            ZoneId zoneId = ZoneId.systemDefault();
            LocalDate localDate = purchase.getFinishDate().minusDays(3);
            Date date = Date.from(localDate.atStartOfDay(zoneId).toInstant());
            if (purchase.getStatus() == StatusPurchase.OPENED || purchase.getStatus() == StatusPurchase.PUBLISHED) {
                Date dateNow = new Date();
                if (date.after(dateNow)) {
                    taskScheduler.schedule(() -> {
                        List<ProducerEntity> producerEntities = producerRepository.getOverallProducers();
                        for (ProducerEntity producer : producerEntities) {
                            telegramSender.sendMessage(producer, String.format(
                                    "Прием заявок на участие в закупке \"%s\" #%d закрывается через 3 дня. Закупка: "
                                            + frontUrl + "/purchases/" + purchase.getPurchaseId(), purchase.getName(), purchase.getNumber()));
                        }
                    }, date);
                }
            }
        }
    }

    /**
     * Уведомление всех поставщиков с предупреждением о закрытии приема заявок на закупку через 1 день
     */
    @AfterReturning(pointcut = "execution(public void com.antikryptonite.outsourcing.services.PurchaseService.createPurchase(..))")
    public void oneDayForClose() {
        List<PurchaseEntity> purchaseEntities = purchaseRepository.findAll();
        for (PurchaseEntity purchase: purchaseEntities) {
            ZoneId zoneId = ZoneId.systemDefault();
            LocalDate localDate = purchase.getFinishDate().minusDays(1);
            Date date = Date.from(localDate.atStartOfDay(zoneId).toInstant());
            if (purchase.getStatus() == StatusPurchase.OPENED || purchase.getStatus() == StatusPurchase.PUBLISHED) {
                Date dateNow = new Date();
                if (date.after(dateNow)) {
                    taskScheduler.schedule(() -> {
                        List<ProducerEntity> producerEntities = producerRepository.getOverallProducers();
                        for (ProducerEntity producer : producerEntities) {
                            telegramSender.sendMessage(producer, String.format(
                                    "Прием заявок на участие в закупке \"%s\" #%d закрывается через 1 день. Закупка: "
                                            + frontUrl + "/purchases/" + purchase.getPurchaseId(), purchase.getName(), purchase.getNumber()));
                        }
                    }, date);
                }
            }
        }
    }

    /**
     * Уведомление об окончании(отмене) закупки
     */
    @AfterReturning(pointcut = "execution(public void com.antikryptonite.outsourcing.services.PurchaseService.closePurchase(..))")
    public void endPurchase(JoinPoint joinPoint) {
        UUID id = (UUID)joinPoint.getArgs()[0];
        PurchaseCloseRequest closeRequest = (PurchaseCloseRequest)joinPoint.getArgs()[1];
        PurchaseEntity purchase = getPurchaseEntity(id);
        if (purchase == null) {
            return;
        }
        if (closeRequest.getStatus() == StatusPurchase.CANCELED) {
            producerRepository.getOverallProducers().forEach(
                    producer -> telegramSender.sendMessage(producer, String.format("Закупка \"%s\" #%d отменена. Закупка: "
                            + frontUrl + "/purchases/" + purchase.getPurchaseId(), purchase.getName(), purchase.getNumber())));
        }
        if (closeRequest.getStatus() == StatusPurchase.CLOSED) {
            producerRepository.getOverallProducers().forEach(
                    producer -> telegramSender.sendMessage(producer, String.format("Прием заявок на участие в закупке \"%s\" #%d закрыт. Закупка: "
                            + frontUrl + "/purchases/" + purchase.getPurchaseId() + " (" + closeRequest.getClosingDescription() + ")", purchase.getName(), purchase.getNumber())));
        }
    }

    /**
     * Уведомление для участников закупки о публикации результатов
     */
    @AfterReturning(pointcut = "execution(public com.antikryptonite.outsourcing.entities.StatusPurchase " +
            "com.antikryptonite.outsourcing.services.PurchaseService.calculatePurchaseStatus(..))", returning = "returnValue")
    public void finishPurchase(JoinPoint joinPoint, Object returnValue) {
        if (returnValue != StatusPurchase.FINISHED) {
            return;
        }
        PurchaseEntity purchase = (PurchaseEntity)joinPoint.getArgs()[0];
        List<ProducerEntity> producerEntities = new ArrayList<>();
        for (ApplicationEntity x: purchase.getApplications()) {
            producerEntities.add(x.getProducer());
        }
        producerEntities.forEach(producer -> {
            telegramSender.sendMessage(producer, String.format("Опубликованы результаты закупки \"%s\" #%d. Закупка: "
                + frontUrl + "/purchases/" + purchase.getPurchaseId(), purchase.getName(), purchase.getNumber()));

            EmailMessage emailMessage = new EmailMessage();
            emailMessage.setEmail(producer.getUser().getLogin());
            emailMessage.setSubject("Результаты конкурса на закупку");
            emailMessage.setText(String.format("Опубликованы результаты закупки \"%s\" #%d. Закупка: "
                    + frontUrl + "/purchases/" + purchase.getPurchaseId(), purchase.getName(), purchase.getNumber()));
            emailSender.send(emailMessage);
        });
    }

    private Runnable sendMessageEveryone(PurchaseEntity purchase) {
        return () -> {
            List<ProducerEntity> producerEntities = producerRepository.findAll();
            for (ProducerEntity producer : producerEntities) {
                telegramSender.sendMessage(producer, String.format(
                        "Прием заявок на участие в закупке \"%s\" #%d открыт. Закупка: "
                                + frontUrl + "/purchases/" + purchase.getPurchaseId(), purchase.getName(), purchase.getNumber()));
            }
        };
    }

    private ProducerEntity getProducerEntity(UUID userId) {
        return producerRepository.findByUserId(userId).orElseGet(() -> {
            log.warning("Couldn't find producer");
            return null;
        });
    }

    private PurchaseEntity getPurchaseEntity(UUID purchaseId) {
        return purchaseRepository.findById(purchaseId).orElseGet(() -> {
            log.warning("Couldn't find purchase");
            return null;
        });
    }
}
