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

    @ParameterizedTest
    @ValueSource(strings = {"0", "4", "-1", "99"})
    @DisplayName("Invalid Range: Should throw IllegalArgumentException")
    void testGetInputNumber_OutOfRange(String input) {
        UI ui = createUIWithInput(input);
        assertThrows(IllegalArgumentException.class, () -> ui.getInputNumber(3));
    }

    @Test
    @DisplayName("Schould Handel Inputs with Spaces")
    void testGetInputNumber_WithSpaces() {
        UI ui = createUIWithInput(" 2 ");
        assertEquals(2, ui.getInputNumber(3), "UI should trim whitespace from input");
    }

    @Test
    @DisplayName("Should Throw corect Exception on non-numeric strings")
    void testGetInputNumber_NotANumber() {
        UI ui = createUIWithInput("abc");
        assertThrows(NumberFormatException.class, () -> ui.getInputNumber(3),
                "Should throw NumberFormatException for non-numeric strings");
    }

    @Test
    @DisplayName("User friendly NonNumeric check")
    void testFriendlyNonNumericError() {
        UI ui = createUIWithInput("abc");
        try {
            ui.getInputNumber(4);
            assertTrue(outContent.toString().contains("not a valid number"));
        } catch (NumberFormatException e) {
            fail("The UI crashed with NumberFormatException instead of showing a error message!");
        }
    }

    @Test
    @DisplayName("Decimal input check")
    void testGetInputNumber_Decimal() {
        UI ui = createUIWithInput("1.0");
        assertThrows(NumberFormatException.class, () -> ui.getInputNumber(3));
    }

    @Test
    @DisplayName("User friendly Decimal check")
    void testFriendlyDecimalError() {
        UI ui = createUIWithInput("1.5");

        try {
            ui.getInputNumber(4);
            String output = outContent.toString();
            assertTrue(output.contains("not a valid whole number"),
                    "The UI should have printed a error message.");
        } catch (NumberFormatException e) {
            fail("The UI crashed with a NumberFormatException! The UI schould printed a error message! ");
        }
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
