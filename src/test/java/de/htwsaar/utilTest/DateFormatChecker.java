package de.htwsaar.utilTest;

import de.htwsaar.util.DateFormatChecker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class DateFormatCheckerTest {

    @ParameterizedTest
    @DisplayName("DateFormatChecker: Valid dates should return true")
    @ValueSource(strings = {
            "2026-01-20",
            "2000-12-31",
            "1990-05-01",
            "2024-02-29"
    })
    void testValidDates(String date) {
        assertTrue(DateFormatChecker.check(date), "Date should be valid: " + date);
        System.err.println("\u001B[32m✔ DateFormatChecker: Valid format accepted [" + date + "]\u001B[0m");
    }

    @ParameterizedTest
    @DisplayName("DateFormatChecker: Invalid dates should return false")
    @ValueSource(strings = {
            "20-01-2026",
            "2026/01/20",
            "2026-1-20",
            "2026-01-5",
            "26-01-20",
            "2026-13-01",
            "2026-01-32",
            "abcd-ef-gh",
            ""
    })
    void testInvalidDates(String date) {
        assertFalse(DateFormatChecker.check(date), "Date should be invalid: " + date);
        System.err.println("\u001B[32m✔ DateFormatChecker: Invalid format correctly rejected [" + date + "]\u001B[0m");
    }
}

