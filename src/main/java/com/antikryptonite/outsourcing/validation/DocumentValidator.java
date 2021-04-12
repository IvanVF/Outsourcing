package com.antikryptonite.outsourcing.validation;

import lombok.extern.java.Log;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.*;
import java.util.*;
import java.util.logging.Level;

/**
 * Валидатор для проверки файла на его тип
 */
@Log
public class DocumentValidator implements ConstraintValidator<DocumentConstraint, MultipartFile> {

    private static final Set<String> allowedTypes = new HashSet<>();

    static {
        allowedTypes.add("image/jpeg");
        allowedTypes.add("image/png");
        allowedTypes.add("application/vnd.ms-excel");
        allowedTypes.add("application/msword");
        allowedTypes.add("application/pdf");
        allowedTypes.add("text/rtf");
        allowedTypes.add("application/rtf");
        allowedTypes.add("application/vnd.ms-powerpoint");
        allowedTypes.add("application/vnd.openxmlformats-officedocument.presentationml.presentation");
        allowedTypes.add("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    }

    @Override
    public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        if (value.isEmpty()) {
            return false;
        }

        log.log(Level.INFO, String.format("Content-type: %s", value.getContentType()));
        return allowedTypes.contains(value.getContentType());
    }
}
