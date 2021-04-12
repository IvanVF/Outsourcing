package com.antikryptonite.outsourcing.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Сериализатор для LocalDate -> гггг-ММ-дд
 */
public class LocalDateSerializer extends StdSerializer<LocalDate> {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    /**
     * Конструктор
     */
    public LocalDateSerializer() {
        this(null);
    }

    /**
     * Конструктор
     *
     * @param t Class
     */
    public LocalDateSerializer(Class t) {
        super(t);
    }

    @Override
    public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(formatter.format(value));
    }
}
