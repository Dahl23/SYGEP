package com.sygep.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class Dates {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Dates() {
    }

    public static String format(LocalDateTime value) {
        return value == null ? "" : value.format(FORMATTER);
    }
}