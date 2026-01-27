package de.htwsaar.communication;

import de.htwsaar.application.CertificateService;
import de.htwsaar.application.EmployeeService;
import de.htwsaar.application.SkillService;
import de.htwsaar.MuteConsoleExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MuteConsoleExtension.class)
class ConsoleUITest {

    private EmployeeService empServiceMock;
    private SkillService skillServiceMock;
    private CertificateService certServiceMock;
    private ConsoleUI sut;

    @BeforeEach
    void setUp() {
        empServiceMock = createMock(EmployeeService.class);
        skillServiceMock = createMock(SkillService.class);
        certServiceMock = createMock(CertificateService.class);
        sut = new ConsoleUI();
    }

    @Test
    @DisplayName("ConsoleUI: Startup-Test")
    void testStartAndExit() {
        String input = "5\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        InputStream originalIn = System.in;
        System.setIn(in);

        try {
            replay(empServiceMock, skillServiceMock, certServiceMock);

            assertDoesNotThrow(() -> {
                sut.start(empServiceMock, skillServiceMock, certServiceMock);
            });

            verify(empServiceMock, skillServiceMock, certServiceMock);
        } finally {
            System.setIn(originalIn);
        }
    }
}
