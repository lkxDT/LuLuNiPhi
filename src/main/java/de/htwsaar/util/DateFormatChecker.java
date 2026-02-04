package de.htwsaar.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Utility class for validating ISO date strings.
 * <p>
 * Uses a strict {@link DateTimeFormatter} to ensure that
 * dates follow the format yyyy-MM-dd and represent valid
 * calendar dates.
 */
public final class DateFormatChecker {

    /**
     * Private constructor to prevent instantiation.
     */
    private DateFormatChecker() {
        // utility class
    }

    /**
     * Strict ISO date formatter (yyyy-MM-dd).
     */
    private static final DateTimeFormatter ISO_STRICT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd")
                    .withResolverStyle(ResolverStyle.STRICT);


    /**
     * Checks whether a string is a valid ISO date (yyyy-MM-dd).
     *
     * @param date date string to validate
     * @return true if the date is valid, otherwise false
     */
    public static boolean check(String date) {
        try {
            LocalDate.parse(date, ISO_STRICT);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}