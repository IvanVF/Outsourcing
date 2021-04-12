package com.antikryptonite.outsourcing.services;

import com.antikryptonite.outsourcing.entities.EntityProducerEntity;
import com.antikryptonite.outsourcing.entities.IndividualProducerEntity;
import com.antikryptonite.outsourcing.entities.ProducerEntity;
import com.antikryptonite.outsourcing.exceptions.ResourceNotFoundException;
import com.antikryptonite.outsourcing.repositories.DocumentRepository;
import com.antikryptonite.outsourcing.repositories.EntityProducerRepository;
import com.antikryptonite.outsourcing.repositories.PhysicalProducerRepository;
import com.antikryptonite.outsourcing.repositories.ProducerRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Сервис создания карточки поставщика
 */
@Service
@Transactional
public class PdfService {

    private final ProducerRepository producerRepository;
    private final EntityProducerRepository entityProducerRepository;
    private final PhysicalProducerRepository physicalProducerRepository;
    private final DocumentRepository documentRepository;

    /**
     * Конструктор сервиса
     */
    @Autowired
    public PdfService(EntityProducerRepository entityProducerRepository,
                      PhysicalProducerRepository physicalProducerRepository,
                      ProducerRepository producerRepository,
                      DocumentRepository documentRepository) {
        this.entityProducerRepository = entityProducerRepository;
        this.physicalProducerRepository = physicalProducerRepository;
        this.producerRepository = producerRepository;
        this.documentRepository = documentRepository;
    }

    /**
     * Метод создаёт файл pdf, преобразует файл в ByteArrayResource, удаляет файл, возвращает ResponseEntity<ByteArrayResource>
     */
    public ResponseEntity<ByteArrayResource> createPdf(UUID userId) throws IOException, DocumentException, ResourceNotFoundException {
        ProducerEntity producerEntity = producerRepository.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException(String.format("Producer %s", userId)));
        EntityProducerEntity entityProducerEntity = null;
        IndividualProducerEntity individualProducerEntity;

        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        PdfWriter.getInstance(document, new FileOutputStream("documents\\" + producerEntity.getInn() + ".pdf"));
        document.open();

        FontFactory.register("TimesNewRoman.ttf", "timesNewRomanFont");
        Font tNRFont = FontFactory.getFont("timesNewRomanFont", "CP1251");

        Paragraph header;
        if (producerEntity.isIndividual()) {
            individualProducerEntity = physicalProducerRepository.getByProducer(producerEntity).orElseThrow(IllegalStateException::new);
            header = new Paragraph(individualProducerEntity.getLastName() + " " +
                    individualProducerEntity.getFirstName() + " " +
                    individualProducerEntity.getMiddleName(), tNRFont);
            header.setSpacingAfter(20);
            header.setAlignment(Element.ALIGN_CENTER);
        } else {
            entityProducerEntity = entityProducerRepository.getByProducer(producerEntity).orElseThrow(IllegalStateException::new);
            header = new Paragraph(entityProducerEntity.getOrganizationName(), tNRFont);
            header.setSpacingAfter(20);
            header.setAlignment(Element.ALIGN_CENTER);
        }

        Paragraph paragraph1 = new Paragraph("Основные сведения", tNRFont);
        paragraph1.setSpacingAfter(20);
        paragraph1.setAlignment(Element.ALIGN_CENTER);

        PdfPTable baseInformationTable = new PdfPTable(2);

        PdfPCell cell = new PdfPCell(new Phrase("ИНН", tNRFont));
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        baseInformationTable.addCell(cell);

        cell = new PdfPCell(new Phrase(producerEntity.getInn(), tNRFont));
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        baseInformationTable.addCell(cell);

        if (producerEntity.isIndividual()) {
            cell = new PdfPCell(new Phrase("Физическое лицо", tNRFont));
        } else {
            cell = new PdfPCell(new Phrase("Юридическое лицо", tNRFont));
        }
        cell.setColspan(2);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        baseInformationTable.addCell(cell);

        cell = new PdfPCell(new Phrase("Технологический стек", tNRFont));
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        baseInformationTable.addCell(cell);

        String stack = "";
        for (int i = 0; i < producerEntity.getStacks().size(); i++) {
            if (i != 0) {
                stack = stack + ", ";
            }
            stack = stack + producerEntity.getStacks().get(i).getTechnology();
        }
        cell = new PdfPCell(new Phrase(stack, tNRFont));
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        baseInformationTable.addCell(cell);

        cell = new PdfPCell(new Phrase("Сфера деятельности", tNRFont));
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        baseInformationTable.addCell(cell);

        cell = new PdfPCell(new Phrase(producerEntity.getSpecialization(), tNRFont));
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        baseInformationTable.addCell(cell);

        Paragraph paragraph2 = new Paragraph("Контактная информация", tNRFont);
        paragraph2.setSpacingAfter(20);
        paragraph2.setSpacingBefore(20);
        paragraph2.setAlignment(Element.ALIGN_CENTER);

        PdfPTable contactInformationTable = new PdfPTable(2);

        PdfPCell cell2;
        for (int i = 0; i < producerEntity.getPhones().size(); i++) {
            cell2 = new PdfPCell(new Phrase("Телефон", tNRFont));
            cell2.setVerticalAlignment(Element.ALIGN_CENTER);
            contactInformationTable.addCell(cell2);

            cell2 = new PdfPCell(new Phrase(String.valueOf(producerEntity.getPhones().get(i).getPhone()), tNRFont));
            cell2.setVerticalAlignment(Element.ALIGN_CENTER);
            contactInformationTable.addCell(cell2);
        }

        Paragraph paragraph3 = new Paragraph();
        PdfPTable personalTable = new PdfPTable(4);
        if (!producerEntity.isIndividual()) {
            cell2 = new PdfPCell(new Phrase("ФИО контактного лица организации", tNRFont));
            cell2.setVerticalAlignment(Element.ALIGN_CENTER);
            contactInformationTable.addCell(cell2);

            String contactPersonFIO = "";
            if (entityProducerEntity.getLastName() != null) {
                contactPersonFIO = contactPersonFIO + entityProducerEntity.getLastName() + " ";
            }
            if (entityProducerEntity.getFirstName() != null) {
                contactPersonFIO = contactPersonFIO + entityProducerEntity.getFirstName() + " ";
            }
            if (entityProducerEntity.getMiddleName() != null) {
                contactPersonFIO = contactPersonFIO + entityProducerEntity.getMiddleName();
            }
            cell2 = new PdfPCell(new Phrase(contactPersonFIO, tNRFont));
            cell2.setVerticalAlignment(Element.ALIGN_CENTER);
            contactInformationTable.addCell(cell2);

            cell2 = new PdfPCell(new Phrase("Должность контактного лица", tNRFont));
            cell2.setVerticalAlignment(Element.ALIGN_CENTER);
            contactInformationTable.addCell(cell2);

            String position = "";
            if (entityProducerEntity.getPosition() != null) {
                position = entityProducerEntity.getPosition();
            }
            cell2 = new PdfPCell(new Phrase(position, tNRFont));
            cell2.setVerticalAlignment(Element.ALIGN_CENTER);
            contactInformationTable.addCell(cell2);

            cell2 = new PdfPCell(new Phrase("Фактический адрес компании", tNRFont));
            cell2.setVerticalAlignment(Element.ALIGN_CENTER);
            contactInformationTable.addCell(cell2);

            cell2 = new PdfPCell(new Phrase(producerEntity.getActualAddress(), tNRFont));
            cell2.setVerticalAlignment(Element.ALIGN_CENTER);
            contactInformationTable.addCell(cell2);

            cell2 = new PdfPCell(new Phrase("Юридический адрес компании", tNRFont));
            cell2.setVerticalAlignment(Element.ALIGN_CENTER);
            contactInformationTable.addCell(cell2);

            cell2 = new PdfPCell(new Phrase(producerEntity.getLegalAddress(), tNRFont));
            cell2.setVerticalAlignment(Element.ALIGN_CENTER);
            contactInformationTable.addCell(cell2);

            cell2 = new PdfPCell(new Phrase("Информация о представительствах", tNRFont));
            cell2.setVerticalAlignment(Element.ALIGN_CENTER);
            contactInformationTable.addCell(cell2);

            String agencies = "";
            if (producerEntity.getAgencies() != null) {
                agencies = producerEntity.getAgencies();
            }
            cell2 = new PdfPCell(new Phrase(agencies, tNRFont));
            cell2.setVerticalAlignment(Element.ALIGN_CENTER);
            contactInformationTable.addCell(cell2);

            paragraph3 = new Paragraph("Структура", tNRFont);
            paragraph3.setSpacingAfter(20);
            paragraph3.setSpacingBefore(20);
            paragraph3.setAlignment(Element.ALIGN_CENTER);

            PdfPCell cell3 = new PdfPCell(new Phrase("Общее число сотрудников", tNRFont));
            cell3.setColspan(3);
            cell3.setVerticalAlignment(Element.ALIGN_CENTER);
            personalTable.addCell(cell3);

            cell3 = new PdfPCell(new Phrase(String.valueOf(producerEntity.getHeadcount()), tNRFont));
            cell3.setColspan(1);
            cell3.setVerticalAlignment(Element.ALIGN_CENTER);
            personalTable.addCell(cell3);

            for (int i = 0; i < producerEntity.getStaffs().size(); i++) {
                cell3 = new PdfPCell(new Phrase("Должность", tNRFont));
                cell3.setVerticalAlignment(Element.ALIGN_CENTER);
                personalTable.addCell(cell3);

                cell3 = new PdfPCell(new Phrase(producerEntity.getStaffs().get(i).getActivity(), tNRFont));
                cell3.setVerticalAlignment(Element.ALIGN_CENTER);
                personalTable.addCell(cell3);

                cell3 = new PdfPCell(new Phrase("Количество", tNRFont));
                cell3.setVerticalAlignment(Element.ALIGN_CENTER);
                personalTable.addCell(cell3);

                cell3 = new PdfPCell(new Phrase(String.valueOf(producerEntity.getStaffs().get(i).getHeadCount()), tNRFont));
                cell3.setVerticalAlignment(Element.ALIGN_CENTER);
                personalTable.addCell(cell3);
            }
        }

        cell2 = new PdfPCell(new Phrase("Сайт компании", tNRFont));
        cell2.setVerticalAlignment(Element.ALIGN_CENTER);
        contactInformationTable.addCell(cell2);

        String url = "";
        if (producerEntity.getUrl() != null) {
            url = producerEntity.getUrl();
        }
        cell2 = new PdfPCell(new Phrase(url, tNRFont));
        cell2.setVerticalAlignment(Element.ALIGN_CENTER);
        contactInformationTable.addCell(cell2);

        Paragraph paragraph4 = new Paragraph("Портфолио", tNRFont);
        paragraph4.setSpacingAfter(20);
        paragraph4.setSpacingBefore(20);
        paragraph4.setAlignment(Element.ALIGN_CENTER);

        PdfPTable portfolioTable = new PdfPTable(4);

        PdfPCell cell4;
        if (producerEntity.getPortfolios() != null) {
            for (int i = 0; i < producerEntity.getPortfolios().size(); i++) {
                cell4 = new PdfPCell(new Phrase("Заказчик", tNRFont));
                cell4.setVerticalAlignment(Element.ALIGN_CENTER);
                portfolioTable.addCell(cell4);

                cell4 = new PdfPCell(new Phrase(producerEntity.getPortfolios().get(i).getCustomer(), tNRFont));
                cell4.setVerticalAlignment(Element.ALIGN_CENTER);
                portfolioTable.addCell(cell4);

                cell4 = new PdfPCell(new Phrase("Описание", tNRFont));
                cell4.setVerticalAlignment(Element.ALIGN_CENTER);
                portfolioTable.addCell(cell4);

                cell4 = new PdfPCell(new Phrase(producerEntity.getPortfolios().get(i).getDescription(), tNRFont));
                cell4.setVerticalAlignment(Element.ALIGN_CENTER);
                portfolioTable.addCell(cell4);
            }
        }

        document.add(header);
        document.add(paragraph1);
        document.add(baseInformationTable);
        document.add(paragraph2);
        document.add(contactInformationTable);
        if (!producerEntity.isIndividual()) {
            document.add(paragraph3);
        }
        if (!producerEntity.isIndividual()) {
            document.add(personalTable);
        }
        document.add(paragraph4);
        document.add(portfolioTable);

        document.close();

        File file = new File("documents\\" + producerEntity.getInn() + ".pdf");
        byte[] data = Files.readAllBytes(Paths.get("documents\\" + producerEntity.getInn() + ".pdf"));
        ByteArrayResource resource = new ByteArrayResource(data);
        long fileLength = file.length();
        file.delete();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(fileLength)
                .body(resource);
    }
}



