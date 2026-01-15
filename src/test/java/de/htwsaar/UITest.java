package de.htwsaar;

import de.htwsaar.application.EmployeeService;
import de.htwsaar.application.SkillService;
import de.htwsaar.communication.UI;
import de.htwsaar.repository.EmployeeRepository;
import de.htwsaar.repositoryTest.MockEmployeeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class UITest {
    private EmployeeService ems;
    private SkillService sks;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outContent));
        EmployeeRepository mockRepo = new MockEmployeeRepository();
        this.ems = new EmployeeService(mockRepo);
        this.sks = new SkillService(null, null);
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    private UI createUIWithInput(String data) {
        Scanner fakeScanner = new Scanner(new ByteArrayInputStream(data.getBytes()));
        return new UI(fakeScanner, ems, sks);
    }

    // --- DIE WIEDERHERGESTELLTEN VALIDATION TESTS ---

    @ParameterizedTest
    @ValueSource(strings = {"1", "2", "3"})
    @DisplayName("Valid Input: Should return correct integers")
    void testGetInputNumber_Valid(String input) {
        UI ui = createUIWithInput(input + "\n");
        assertEquals(Integer.parseInt(input), ui.getInputNumber(3));
        System.err.println("\u001B[32m✔ Validation: Correctly parsed valid integer " + input + "\u001B[0m");
    }

    @Test
    @DisplayName("Invalid Range: Should ask again and return valid input")
    void testGetInputNumber_OutOfRange() {
        UI ui = createUIWithInput("0\n99\n2\n");
        int result = ui.getInputNumber(3);
        assertEquals(2, result);
        assertTrue(outContent.toString().contains("between 1 and 3"));
        System.err.println("\u001B[32m✔ Validation: Correctly handled out-of-range inputs\u001B[0m");
    }

    @Test
    @DisplayName("Should Handle Inputs with Spaces")
    void testGetInputNumber_WithSpaces() {
        UI ui = createUIWithInput(" 2 \n");
        assertEquals(2, ui.getInputNumber(3));
        System.err.println("\u001B[32m✔ Validation: Successfully handled whitespace in line\u001B[0m");
    }

    @Test
    @DisplayName("Should notify user on non-numeric strings")
    void testGetInputNumber_NotANumber() {
        UI ui = createUIWithInput("abc\n1\n");
        ui.getInputNumber(3);
        String output = outContent.toString();
        assertTrue(output.contains("whole number"));
        System.err.println("\u001B[32m✔ Validation: Identified and rejected non-numeric string 'abc'\u001B[0m");
    }

    @Test
    @DisplayName("Decimal input check - no longer throws exception")
    void testGetInputNumber_Decimal() {
        UI ui = createUIWithInput("1.0\n2\n");
        int result = ui.getInputNumber(3);
        assertEquals(2, result);
        assertTrue(outContent.toString().contains("decimals"));
        System.err.println("\u001B[32m✔ Validation: Decimals correctly blocked without crashing\u001B[0m");
    }

    @Test
    @DisplayName("Decimal check should not throw exception")
    void testGetInputNumber_Decimal_NoException() {
        UI ui = createUIWithInput("1.0\n1\n");
        assertDoesNotThrow(() -> {
            int result = ui.getInputNumber(3);
            assertEquals(1, result);
        });
        System.err.println("\u001B[32m✔ Validation: Confirmed decimal handling is exception-free\u001B[0m");
    }

    // --- AKTUALISIERTE MENU NAVIGATION TESTS (mit Exit-Pfad) ---

    @Test
    @DisplayName("Navigation: Selection 1 then 1 displays Mia Müller and Exits")
    void testNavigationToEmployeeMenu() {
        UI ui = createUIWithInput("1\n1\n4\n4\n");
        ui.startMainMenu();
        String output = outContent.toString();
        assertTrue(output.contains("Employees Menu:"));
        assertTrue(output.contains("Mia") && output.contains("Müller"));
        System.err.println("\u001B[32m✔ Navigation: Successfully reached Employee List and exited\u001B[0m");
    }

    @Test
    @DisplayName("Selecting '2' in Main Menu should show Skills Menu and Exit")
    void testNavigationToSkillsMenu() {
        UI ui = createUIWithInput("2\n4\n4\n");
        ui.startMainMenu();
        String output = outContent.toString();
        assertTrue(output.contains("Skills Menu:"));
        System.err.println("\u001B[32m✔ Navigation: Successfully reached Skills Menu and exited\u001B[0m");
    }

    @Test
    @DisplayName("Selecting '3' in Main Menu should show Certificates Menu and Exit")
    void testNavigationToCertificatesMenu() {
        UI ui = createUIWithInput("3\n4\n4\n");
        ui.startMainMenu();
        String output = outContent.toString();
        assertTrue(output.contains("Certificates Menu:"));
        System.err.println("\u001B[32m✔ Navigation: Successfully reached Certificates Menu and exited\u001B[0m");
    }

    @Test
    @DisplayName("Integration: Add Employee and Exit")
    void testAddEmployeeIntegration() {
        String input = "1\n2\nClara\nHahn\nclara@test.de\nDev\nActive\n2026-01-15\n40\n4\n4\n";
        UI ui = createUIWithInput(input);
        ui.startMainMenu();
        assertTrue(outContent.toString().contains("Employee successfully"));
    }
}