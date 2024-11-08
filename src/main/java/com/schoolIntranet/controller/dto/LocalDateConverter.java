package com.schoolIntranet.controller.dto;

import com.opencsv.bean.AbstractBeanField;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LocalDateConverter extends AbstractBeanField<LocalDate, String> {

    @Override
    protected LocalDate convert(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            return LocalDate.parse(value, formatter);
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Error: " + value, e);
        }
    }
}
