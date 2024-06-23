package com.pymntprocessing.pymntprocessing.util;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Converter(autoApply = true)
public class DatabaseDateTimeConverter implements AttributeConverter<LocalDateTime, String> {
    @Override
    public String convertToDatabaseColumn(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return Objects.isNull(localDateTime) ? null : localDateTime.format(formatter);
    }

    @Override
    public LocalDateTime convertToEntityAttribute(String dateTimeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return Objects.isNull(dateTimeString) ? null : LocalDateTime.parse(dateTimeString, formatter);
    }

    public LocalDateTime convertTo_yyyyMMdd_HHmmss(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dateTimeString = Objects.isNull(dateTime) ? null : dateTime.format(formatter);
        return Objects.isNull(dateTime) ? null : LocalDateTime.parse(dateTimeString, formatter);
    }
}