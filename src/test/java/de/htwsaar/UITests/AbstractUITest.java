package de.htwsaar.UITests;

import de.htwsaar.application.EmployeeService;
import de.htwsaar.application.SkillService;
import de.htwsaar.communication.UI;
import de.htwsaar.repositoryTest.MockEmployeeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

public abstract class AbstractUITest {
    protected EmployeeService ems;
    protected SkillService sks;
    protected final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outContent));
        this.ems = new EmployeeService(new MockEmployeeRepository());
        this.sks = new SkillService(null, null);
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    protected UI createUIWithInput(String data) {
        Scanner fakeScanner = new Scanner(new ByteArrayInputStream(data.getBytes()));
        return new UI(fakeScanner, ems, sks);
    }
}
