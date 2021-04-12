package com.antikryptonite.outsourcing.entities;

import com.antikryptonite.outsourcing.dto.response.LocalizedEnum;

/**
 * Роли пользователей
 */
public enum Role implements LocalizedEnum {
    USER("Неаккредитованный поставщик"), ADMIN("Администратор"), LAWYER("Юрист"), SALESMAN("Сотрудник отдела продаж"), PRODUCER("Поставщик"), BAD(
            "Добавленный в ЧС");

    private final String text;

    Role(String text) {
        this.text = text;
    }

    @Override
    public String getLocalizedText() {
        return text;
    }
}
