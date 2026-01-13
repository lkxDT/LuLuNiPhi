package de.htwsaar;

import de.htwsaar.application.EmployeeService;
import de.htwsaar.application.SkillService;
import de.htwsaar.communication.UI;
import de.htwsaar.domain.Employee;
import de.htwsaar.repository.EmployeeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class UITest {
    private EmployeeService ems;
    private SkillService sks;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    // Redirecting System.out to check what the UI prints
    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outContent));
        EmployeeRepository mockRepo = new MockRepository();
        this.ems = new EmployeeService(mockRepo);
        this.sks = new SkillService(null, null);
    }


    // Set System.out back to normal after each test
    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    //Creating Ui with "Fake" input for Tests
    private UI createUIWithInput(String data) {
        Scanner fakeScanner = new Scanner(new ByteArrayInputStream(data.getBytes()));
        return new UI(fakeScanner, ems, sks);
    }


    //Validation Tests
    @ParameterizedTest
    @ValueSource(strings = {"1", "2", "3"})
    @DisplayName("Valid Input: Should return correct integers")
    void testGetInputNumber_Valid(String input) {
        UI ui = createUIWithInput(input);
        assertEquals(Integer.parseInt(input), ui.getInputNumber(3));
    }
    @Test
    @DisplayName("Invalid Range: Should ask again and return valid input")
    void testGetInputNumber_OutOfRange() {
        UI ui = createUIWithInput("0 99 2");
        int result = ui.getInputNumber(3);
        assertEquals(2, result, "The UI should eventually return the valid input '2'");
        String output = outContent.toString();
        assertTrue(output.contains("Please enter a number between 1 and 3"),
                "Should have warned the user about the range.");
    }
    @Test
    @DisplayName("Schould Handel Inputs with Spaces")
    void testGetInputNumber_WithSpaces() {
        UI ui = createUIWithInput(" 2 ");
        assertEquals(2, ui.getInputNumber(3), "UI should trim whitespace from input");
    }
    @Test
    @DisplayName("Should notify user on non-numeric strings")
    void testGetInputNumber_NotANumber() {
        UI ui = createUIWithInput("abc 1");
        ui.getInputNumber(3);
        String output = outContent.toString();
        assertTrue(output.toLowerCase().contains("invalid") || output.contains("whole number"),
                "Should have notified the user that 'abc' is not a valid input.");
    }
    @Test
    @DisplayName("User friendly NonNumeric check")
    void testFriendlyNonNumericError() {
        UI ui = createUIWithInput("abc 1");
        ui.getInputNumber(4);
        String output = outContent.toString();
        assertTrue(output.contains("whole number"),
                "Expected output to mention 'whole number' but got: " + output);
    }
    @Test
    @DisplayName("Decimal input check - no longer throws exception")
    void testGetInputNumber_Decimal() {
        UI ui = createUIWithInput("1.0 2");
        int result = ui.getInputNumber(3);
        assertEquals(2, result, "Should skip the decimal and return the next valid integer.");
        assertTrue(outContent.toString().contains("decimals"), "Should show decimal error message");
    }
    @Test
    @DisplayName("User friendly Decimal check")
    void testFriendlyDecimalError() {
        UI ui = createUIWithInput("1.5 2");
        int result = ui.getInputNumber(4);
        String output = outContent.toString();
        assertTrue(output.contains("decimals"), "Should warn about decimals");
        assertEquals(2, result, "Should eventually return the valid number");
    }
    @Test
    @DisplayName("Decimal check should not throw exception")
    void testGetInputNumber_Decimal_NoException() {
        UI ui = createUIWithInput("1.0 1");
        assertDoesNotThrow(() -> {
            int result = ui.getInputNumber(3);
            assertEquals(1, result);
        });
    }


    //MENU NAVIGATION TESTS
    @Test
    @DisplayName("Navigation: Selection 1 then 1 displays Mia Müller from MockRepo")
    void testNavigationToEmployeeMenu() {
        UI ui = createUIWithInput("1 1");
        ui.startMainMenu();
        String output = outContent.toString();
        try {
            assertTrue(output.contains("Employees Menu:"), "Menu header missing");
            assertTrue(output.contains("1 |"), "ID 1 should be visible");
            assertTrue(output.contains("Mia") && output.contains("Müller"), "Data mismatch");
            System.err.println("\u001B[32m✔ Test Passed: UI correctly displayed Mock data.\u001B[0m");
        } catch (AssertionError e) {
            System.err.println("\u001B[31m✘ Test Failed: UI output did not match expectations.\u001B[0m");
            throw e;
        }
    }
    @Test
    @DisplayName("Selecting '2' in Main Menu should show Skills Menu")
    void testNavigationToSkillsMenu() {
        // Arrange: '2' for Skills, then '1' for show all
        UI ui = createUIWithInput("2\n1");

        // Act
        ui.startMainMenu();

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Skills Menu:"), "Choosing 2 should display Skills Menu");
        assertTrue(output.contains("placeholder: show all skills"), "skills-menu choice should execute");
    }
    @Test
    @DisplayName("Selecting '3' in Main Menu should show Certificates Menu")
    void testNavigationToCertificatesMenu() {
        // Arrange: '3' for Certificates, then '1' for show all
        UI ui = createUIWithInput("3\n1");

        // Act
        ui.startMainMenu();

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Certificates Menu:"), "Choosing 3 should display Certificates Menu");
        assertTrue(output.contains("placeholder: show all certificates"), "certificates-menu choice should execute");
    }
    @Test
    @DisplayName("Selecting '4' in Main Menu should show Projects Menu")
    void testNavigationToProjectsMenu() {
        UI ui = createUIWithInput("4\n1");
        ui.startMainMenu();
        String output = outContent.toString();
        assertTrue(output.contains("Projects Menu:"), "Projects-menu choice should execute");
    }
}
