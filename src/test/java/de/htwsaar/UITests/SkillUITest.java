package de.htwsaar.UITests;

import de.htwsaar.communication.UI;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SkillUITest extends AbstractUITest {
    @Test
    void testNavigationToSkillsMenu() {
        UI ui = createUIWithInput("2\n1\n5\n4\n");
        ui.startMainMenu();
        String output = outContent.toString();
        assertTrue(output.contains("Skills Menu:"));
        assertTrue(output.contains("Exiting the Program..."));
        System.err.println("\u001B[32m✔ Navigation: Successfully reached Skills Menu and exited\u001B[0m");
    }
}
