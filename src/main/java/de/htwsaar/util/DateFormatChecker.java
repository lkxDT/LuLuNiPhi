package de.htwsaar.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

public final class DateFormatChecker {

    private DateFormatChecker() {
        // utility class
    }

    private static final DateTimeFormatter ISO_STRICT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd")
                    .withResolverStyle(ResolverStyle.STRICT);


    public static boolean check(String date) {
        try {
            LocalDate.parse(date, ISO_STRICT);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}