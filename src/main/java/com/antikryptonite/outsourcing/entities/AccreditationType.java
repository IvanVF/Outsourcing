package com.antikryptonite.outsourcing.entities;

/**
 * Типы аккредитации
 */
public enum AccreditationType {
    ACCREDITED("Аккредитован"), UNACCREDITED("Не аккредитован"), BLOCK("В чёрном списке");

    private final String text;

    AccreditationType(String text) {
        this.text = text;
    }

    public String getLocalizedText() {
        return text;
    }
}
