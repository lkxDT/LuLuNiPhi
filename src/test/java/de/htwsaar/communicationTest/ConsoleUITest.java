package de.htwsaar.communicationTest;

import de.htwsaar.application.EmployeeService;
import de.htwsaar.application.SkillService;
import de.htwsaar.application.CertificateService;
import de.htwsaar.communication.ConsoleUI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ConsoleUITest {

    @Test
    @DisplayName("ConsoleUI: Startup-Test")
    void testStartAndExit() {
        EmployeeService empService = null;
        SkillService skillService = null;
        CertificateService certService = null;

        String input = "4\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        InputStream originalIn = System.in;
        System.setIn(in);

        try {
            ConsoleUI ui = new ConsoleUI();
            assertDoesNotThrow(() -> {
                ui.start(empService, skillService, certService);
            });

        } finally {
            System.setIn(originalIn);
        }
        System.err.println("\u001B[32m✔ ConsoleUI: Basic Flow verified\u001B[0m");
    }
}
