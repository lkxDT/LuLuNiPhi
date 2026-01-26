package de.htwsaar.communicationTest;

import de.htwsaar.application.EmployeeService;
import de.htwsaar.application.SkillService;
import de.htwsaar.application.CertificateService;
import de.htwsaar.communication.ConsoleUI;
import de.htwsaar.repositoryTest.MockCertificateRepository;
import de.htwsaar.repositoryTest.MockEmployeeRepository;
import de.htwsaar.repositoryTest.MockSkillRepository;
import de.htwsaar.repositoryTest.MockEmployeeSkillRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ConsoleUITest {

    @Test
    @DisplayName("ConsoleUI: Startup-Test")
    void testStartAndExit() {
        var skillRepo = new MockSkillRepository();
        var certRepo = new MockCertificateRepository();
        var empRepo = new MockEmployeeRepository();
        var empSkillRepo = new MockEmployeeSkillRepository();


        SkillService skillService = new SkillService(skillRepo, empSkillRepo);
        EmployeeService empService = new EmployeeService(empRepo);
        CertificateService certService = new CertificateService(certRepo);

        String input = "5\n";
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

        System.err.println("\u001B[32m✔ ConsoleUI: Basic Flow verified (Logo and Tips muted)\u001B[0m");
    }
}
