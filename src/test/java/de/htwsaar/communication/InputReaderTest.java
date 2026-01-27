package de.htwsaar.communication;

import de.htwsaar.MuteConsoleExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MuteConsoleExtension.class)
class InputReaderTest {

    /**
     * Helper to create an InputReader with a simulated input.
     * @param input The string to be simulated as user input.
     * @return      a InputReader instance.
     */
    private InputReader createReader(String input) {
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        return new InputReader(scanner);
    }

    @Test
    void testReadMenuNumberValid() {
        InputReader sut = createReader("3\n");
        int result = sut.readMenuNumber(5);
        assertEquals(3, result);
    }

    @Test
    void testReadMenuNumberRetryAfterInvalid() {
        InputReader sut = createReader("abc\n10\n4.5\n2\n");
        int result = sut.readMenuNumber(5);
        assertEquals(2, result);
    }

    @Test
    void testReadLine() {
        InputReader sut = createReader("Test String\n");
        String result = sut.readLine();
        assertEquals("Test String", result);
    }

    @Test
    void testReadIntSuccess() {
        InputReader sut = createReader("  42  \n");
        int result = sut.readInt("Error");
        assertEquals(42, result);
    }

    @Test
    void testReadIntThrowsException() {
        InputReader sut = createReader("not-a-number\n");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> sut.readInt("Custom Error Message")
        );

        assertEquals("Custom Error Message", exception.getMessage());
    }

    @Test
    void testReadIntEmptyInput() {
        InputReader sut = createReader("\n");
        assertThrows(IllegalArgumentException.class, () -> sut.readInt("Error"));
    }
}
