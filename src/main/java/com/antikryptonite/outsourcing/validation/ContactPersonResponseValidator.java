package com.antikryptonite.outsourcing.validation;

import com.antikryptonite.outsourcing.dto.response.ContactPersonResponse;

public class ContactPersonResponseValidator {

    /**
     * Метод провекрки правильности вода данных о контактном лице поставщика
     */
    public static boolean validateContactPersonResponse(ContactPersonResponse contactPersonResponse) throws NullPointerException {
        boolean isAnyMistakes = false;
        if (contactPersonResponse.getFirstName().length() < 2 || contactPersonResponse.getFirstName().length() > 40) { isAnyMistakes = true; }
        if (contactPersonResponse.getLastName().length() < 2 || contactPersonResponse.getLastName().length() > 40) { isAnyMistakes = true; }
        if (contactPersonResponse.getMiddleName().length() < 2 || contactPersonResponse.getMiddleName().length() > 40) { isAnyMistakes = true; }
        return isAnyMistakes;
    }

}
