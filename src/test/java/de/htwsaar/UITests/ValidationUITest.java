package de.htwsaar.UITests;

import de.htwsaar.communication.UI;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class ValidationUITest extends AbstractUITest {
    @ParameterizedTest
    @ValueSource(strings = {"1", "2", "3"})
    void testGetInputNumber_Valid(String input) {
        UI ui = createUIWithInput(input + "\n");
        assertEquals(Integer.parseInt(input), ui.getInputNumber(3));
        System.err.println("\u001B[32m✔ Validation: Correctly parsed valid integer " + input + "\u001B[0m");
    }

    @Test
    void testGetInputNumber_OutOfRange() {
        UI ui = createUIWithInput("0\n99\n2\n");
        int result = ui.getInputNumber(3);
        assertEquals(2, result);
        assertTrue(outContent.toString().contains("between 1 and 3"));
        System.err.println("\u001B[32m✔ Validation: Correctly handled out-of-range inputs\u001B[0m");
    }

    @Test
    void testGetInputNumber_WithSpaces() {
        UI ui = createUIWithInput(" 2 \n");
        assertEquals(2, ui.getInputNumber(3));
        System.err.println("\u001B[32m✔ Validation: Successfully handled whitespace in line\u001B[0m");
    }

    @Test
    void testGetInputNumber_NotANumber() {
        UI ui = createUIWithInput("abc\n1\n");
        ui.getInputNumber(3);
        assertTrue(outContent.toString().contains("whole number"));
        System.err.println("\u001B[32m✔ Validation: Identified and rejected non-numeric string 'abc'\u001B[0m");
    }

    @Test
    void testGetInputNumber_Decimal() {
        UI ui = createUIWithInput("1.0\n2\n");
        int result = ui.getInputNumber(3);
        assertEquals(2, result);
        assertTrue(outContent.toString().contains("decimals"));
        System.err.println("\u001B[32m✔ Validation: Decimals correctly blocked without crashing\u001B[0m");
    }
}
