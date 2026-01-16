package de.htwsaar.UITests;

import de.htwsaar.communication.UI;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EmployeeUITest extends AbstractUITest {
    @Test
    void testNavigationToEmployeeMenu() {
        UI ui = createUIWithInput("1\n1\n5\n4\n");
        ui.startMainMenu();
        String output = outContent.toString();
        assertTrue(output.contains("Employees Menu:"));
        assertTrue(output.contains("Mia") && output.contains("Müller"));
        assertTrue(output.contains("Exiting the Program..."));
        System.err.println("\u001B[32m✔ Navigation: Successfully reached Employee Menu and exited\u001B[0m");
    }

    @Test
    void testAddEmployeeIntegration() {
        String input = "1\n2\nClara\nHahn\nclara@test.de\nDev\nActive\n2026-01-15\n40\n5\n4\n";
        UI ui = createUIWithInput(input);
        ui.startMainMenu();
        assertTrue(outContent.toString().contains("Employee successfully"));
        System.err.println("\u001B[32m✔ Integration: Successfully added Employee, returned to Main and exited\u001B[0m");
    }


}
