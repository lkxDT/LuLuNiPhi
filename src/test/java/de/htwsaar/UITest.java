package de.htwsaar;

import de.htwsaar.communication.UI;
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

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    // Redirecting System.out to check what the UI prints
    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    // Set System.out back to normal after each test
    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    //Creating Ui with "Fake" input for Tests
    private UI createUIWithInput(String data) {
        return new UI(new Scanner(new ByteArrayInputStream(data.getBytes())));
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
    @DisplayName("Selecting '1' in Main Menu should show Employee Menu")
    void testNavigationToEmployeeMenu() {
        UI ui = createUIWithInput("1\n1");
        ui.startMainMenu();
        String output = outContent.toString();
        assertTrue(output.contains("Employees Menu:"), "Choosing 1 should display Employee Menu");
        assertTrue(output.contains("placeholder: show all employees"), "employees-menu choice should execute");
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
