package com.antikryptonite.outsourcing.entities;

import com.antikryptonite.outsourcing.dto.response.LocalizedEnum;

/**
 * Статусы закупок
 */
public enum StatusPurchase implements LocalizedEnum {
    PUBLISHED("Опубликована"), OPENED("Открыт приём заявок"), CLOSED("Закрыт приём заявок"), FINISHED("Завершена"),
    CANCELED("Отменена"), ALL("Все");

    StatusPurchase(String localizedText) {
        this.localizedText = localizedText;
    }

    private final String localizedText;

    @Override
    public String getLocalizedText() {
        return localizedText;
    }
}