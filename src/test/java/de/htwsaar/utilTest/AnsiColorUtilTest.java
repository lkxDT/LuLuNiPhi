package de.htwsaar.utilTest;

import de.htwsaar.util.AnsiColorUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class AnsiColorUtilTest {


    private static Stream<Arguments> provideStatusColors() {
        return Stream.of(
                Arguments.of("ACTIVE", 10, AnsiColorUtil.GREEN),
                Arguments.of("INACTIVE", 10, AnsiColorUtil.YELLOW),
                Arguments.of("ON_LEAVE", 10, AnsiColorUtil.RED)
        );
    }

    @ParameterizedTest
    @MethodSource("provideStatusColors")
    @DisplayName("colorizeStatus: Should return correct color codes and handle padding")
    void testColorizeStatus(String status, int width, String expectedColor) {
        String result = AnsiColorUtil.colorizeStatus(status, width);

        assertAll("Check ANSI formatting",
                () -> assertTrue(result.startsWith(expectedColor), "Should start with color code: " + expectedColor.replace("\u001B", "ESC")),
                () -> assertTrue(result.endsWith(AnsiColorUtil.RESET), "Should end with RESET code"),
                () -> assertTrue(result.contains(status.toUpperCase()), "Should contain the status text"),
                () -> assertEquals(width, stripAnsi(result).length(), "Visible width (without ANSI) should be " + width)
        );
        System.err.println("\u001B[32m✔ AnsiColorUtil: Status coloring and padding verified for " + status + "\u001B[0m");
    }

    @Test
    @DisplayName("colorizeStatus: Should handle unknown status without colors")
    void testUnknownStatus() {
        String result = AnsiColorUtil.colorizeStatus("UNKNOWN", 10);
        assertFalse(result.contains("\u001B"), "Plain text should not contain ANSI codes");
        assertEquals("UNKNOWN   ", result);
        System.err.println("\u001B[32m✔ AnsiColorUtil: Unknown status handling verified\u001B[0m");
    }


    @Test
    @DisplayName("colorizeStatus: Should not truncate if status is longer than width")
    void testLongStatus() {
        String longStatus = "VERY_LONG_STATUS_NAME";
        String result = AnsiColorUtil.colorizeStatus(longStatus, 5);

        assertEquals(longStatus.length(), stripAnsi(result).length());
        assertTrue(result.contains(longStatus));
        System.err.println("\u001B[32m✔ AnsiColorUtil: Long status handling verified\u001B[0m");
    }

    private String stripAnsi(String input) {
        return input.replaceAll("\u001B\\[[;\\d]*m", "");
    }
}