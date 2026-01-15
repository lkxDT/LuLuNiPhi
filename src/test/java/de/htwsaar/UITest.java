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

    // --- VALIDATION TESTS ---

    @ParameterizedTest
    @ValueSource(strings = {"1", "2", "3"})
    @DisplayName("Valid Input: Should return correct integers")
    void testGetInputNumber_Valid(String input) {
        UI ui = createUIWithInput(input);
        assertEquals(Integer.parseInt(input), ui.getInputNumber(3));
        System.err.println("\u001B[32m✔ Validation: Correctly parsed valid integer " + input + "\u001B[0m");
    }

    @Test
    @DisplayName("Invalid Range: Should ask again and return valid input")
    void testGetInputNumber_OutOfRange() {
        UI ui = createUIWithInput("0 99 2");
        int result = ui.getInputNumber(3);
        assertEquals(2, result);
        assertTrue(outContent.toString().contains("between 1 and 3"));
        System.err.println("\u001B[32m✔ Validation: Correctly handled out-of-range inputs\u001B[0m");
    }

    @Test
    @DisplayName("Should Handle Inputs with Spaces")
    void testGetInputNumber_WithSpaces() {
        UI ui = createUIWithInput(" 2 ");
        assertEquals(2, ui.getInputNumber(3));
        System.err.println("\u001B[32m✔ Validation: Successfully trimmed whitespace from input\u001B[0m");
    }

    @Test
    @DisplayName("Should notify user on non-numeric strings")
    void testGetInputNumber_NotANumber() {
        UI ui = createUIWithInput("abc 1");
        ui.getInputNumber(3);
        String output = outContent.toString();
        assertTrue(output.toLowerCase().contains("invalid") || output.contains("whole number"));
        System.err.println("\u001B[32m✔ Validation: Identified and rejected non-numeric string 'abc'\u001B[0m");
    }

    @Test
    @DisplayName("User friendly NonNumeric check")
    void testFriendlyNonNumericError() {
        UI ui = createUIWithInput("abc 1");
        ui.getInputNumber(4);
        assertTrue(outContent.toString().contains("whole number"));
        System.err.println("\u001B[32m✔ Validation: Friendly error message shown for non-numeric input\u001B[0m");
    }

    @Test
    @DisplayName("Decimal input check - no longer throws exception")
    void testGetInputNumber_Decimal() {
        UI ui = createUIWithInput("1.0 2");
        int result = ui.getInputNumber(3);
        assertEquals(2, result);
        assertTrue(outContent.toString().contains("decimals"));
        System.err.println("\u001B[32m✔ Validation: Decimals correctly blocked without crashing\u001B[0m");
    }

    @Test
    @DisplayName("User friendly Decimal check")
    void testFriendlyDecimalError() {
        UI ui = createUIWithInput("1.5 2");
        int result = ui.getInputNumber(4);
        assertTrue(outContent.toString().contains("decimals"));
        assertEquals(2, result);
        System.err.println("\u001B[32m✔ Validation: User informed that decimals are not allowed\u001B[0m");
    }

    @Test
    @DisplayName("Decimal check should not throw exception")
    void testGetInputNumber_Decimal_NoException() {
        UI ui = createUIWithInput("1.0 1");
        assertDoesNotThrow(() -> {
            int result = ui.getInputNumber(3);
            assertEquals(1, result);
        });
        System.err.println("\u001B[32m✔ Validation: Confirmed decimal handling is exception-free\u001B[0m");
    }

    // --- MENU NAVIGATION TESTS ---

    @Test
    @DisplayName("Navigation: Selection 1 then 1 displays Mia Müller from MockRepo")
    void testNavigationToEmployeeMenu() {
        UI ui = createUIWithInput("1 1");
        ui.startMainMenu();
        String output = outContent.toString();
        try {
            assertTrue(output.contains("Employees Menu:"));
            assertTrue(output.contains("1 |"));
            assertTrue(output.contains("Mia") && output.contains("Müller"));
            System.err.println("\u001B[32m✔ Navigation: Successfully reached Employee List via menus\u001B[0m");
        } catch (AssertionError e) {
            System.err.println("\u001B[31m✘ Navigation Failed: Employee data not found in output\u001B[0m");
            throw e;
        }
    }

    @Test
    @DisplayName("Selecting '2' in Main Menu should show Skills Menu")
    void testNavigationToSkillsMenu() {
        UI ui = createUIWithInput("2\n1");
        ui.startMainMenu();
        String output = outContent.toString();
        assertTrue(output.contains("Skills Menu:"));
        assertTrue(output.contains("placeholder: show all skills"));
        System.err.println("\u001B[32m✔ Navigation: Successfully reached Skills Menu\u001B[0m");
    }

    @Test
    @DisplayName("Selecting '3' in Main Menu should show Certificates Menu")
    void testNavigationToCertificatesMenu() {
        UI ui = createUIWithInput("3\n1");
        ui.startMainMenu();
        String output = outContent.toString();
        assertTrue(output.contains("Certificates Menu:"));
        assertTrue(output.contains("placeholder: show all certificates"));
        System.err.println("\u001B[32m✔ Navigation: Successfully reached Certificates Menu\u001B[0m");
    }

    @Test
    @DisplayName("Selecting '4' in Main Menu should show Projects Menu")
    void testNavigationToProjectsMenu() {
        UI ui = createUIWithInput("4\n1");
        ui.startMainMenu();
        assertTrue(outContent.toString().contains("Projects Menu:"));
        System.err.println("\u001B[32m✔ Navigation: Successfully reached Projects Menu\u001B[0m");
    }

    // --- INTEGRATION TESTS ---

    @Test
    @DisplayName("Integration: Add Employee and verify Loop + Data Persistence")
    void testAddEmployeeAndLoopVerification() {
        String input = "1 2 Clara Hahn clara@test.de Dev Active 2026-01-15 40 1 0 0";
        UI ui = createUIWithInput(input);
        ui.startMainMenu();

        String output = outContent.toString();

        assertTrue(output.contains("Employee successfully"),
                "\u001B[31m[UI ERROR] The success message was never printed. Check try-catch block!\u001B[0m");


        int successIndex = output.indexOf("Employee successfully");

        String outputAfterSuccess = output.substring(successIndex);
        boolean nameFoundInList = outputAfterSuccess.contains("Clara") && outputAfterSuccess.contains("Hahn");
        if (!nameFoundInList) {
            fail("\u001B[31m[LOOP OR PERSISTENCE ERROR] 'Employee successfully' was printed, but the list never showed up again. " +
                    "Menu is likely missing the 'while(running)' loop or the 'Show List' logic is broken.\u001B[0m");
        }

        System.err.println("\u001B[32m✔ Integration: Full Add-Loop-List cycle verified successfully!\u001B[0m");
    }
        @Test
        @DisplayName("UI: Should catch Service exception and print user-friendly error")
        void testUICatchesServiceException() {
            UI ui = createUIWithInput("1 2 Max Power max@test.de Dev Active 2026-01-13 200");
            ui.startMainMenu();
            String output = outContent.toString();

            try {
                assertTrue(output.contains("Error") || output.contains("invalid"));
                System.err.println("\u001B[32m✔ Integration: UI correctly caught Service-layer Exception and notified user\u001B[0m");
            } catch (AssertionError e) {
                System.err.println("\u001B[31m✘ Integration Failed: UI crashed or failed to show error for 200-hour input\u001B[0m");
                throw e;
            }
        }
    }