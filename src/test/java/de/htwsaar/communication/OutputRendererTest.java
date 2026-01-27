package de.htwsaar.communication;

import de.htwsaar.domain.AssignedCertificate;
import de.htwsaar.domain.AssignedSkill;
import de.htwsaar.domain.Certificate;
import de.htwsaar.domain.Employee;
import de.htwsaar.domain.Skill;
import de.htwsaar.util.AnsiColorUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class OutputRendererTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private OutputRenderer sut;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
        sut = new OutputRenderer();
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }

    /**
     * Helper to check if the captured output contains a string.
     * @param part The string to look for.
     * @return     true if contains.
     */
    private boolean outContains(String part) {
        return outContent.toString().contains(part);
    }

    @Test
    void testSectionRendering() {
        sut.section("Test Title");
        assertTrue(outContains(AnsiColorUtil.GREEN + "Test Title" + AnsiColorUtil.RESET));
        assertTrue(outContains("=================================================="));
    }

    @Test
    void testInfoMessage() {
        sut.info("Information Message");
        assertTrue(outContains(AnsiColorUtil.YELLOW + "[INFO] " + AnsiColorUtil.RESET + "Information Message"));
    }

    @Test
    void testErrorMessage() {
        sut.error("Error Message");
        assertTrue(outContains(AnsiColorUtil.RED + "[ERROR] " + AnsiColorUtil.RESET + "Error Message"));
    }

    @Test
    void testEmployeeTableEmpty() {
        sut.employeeTable(Collections.emptyList());
        assertTrue(outContains("No employees found."));
    }

    @Test
    void testEmployeeTableContent() {
        Employee emp = new Employee(1, "John", "Doe", "j@co.com", "Dev", "Active", "2026-01-01", 40);
        sut.employeeTable(List.of(emp));

        assertTrue(outContains("John"));
        assertTrue(outContains("Doe"));
        assertTrue(outContains("Dev"));
    }

    @Test
    void testSkillTableContent() {
        Skill skill = new Skill(10, "Java", "Backend");
        sut.skillTable(List.of(skill));

        assertTrue(outContains("10"));
        assertTrue(outContains("Java"));
        assertTrue(outContains("Backend"));
    }

    @Test
    void testCertificateTableContent() {
        Certificate cert = new Certificate(1, "OCP", "IT", "External", true, 24, "Active");
        sut.certificateTable(List.of(cert));

        assertTrue(outContains("OCP"));
        assertTrue(outContains("YES"));
        assertTrue(outContains("24"));
    }

    @Test
    void testAssignedSkillTable() {
        AssignedSkill skill = new AssignedSkill(1, "SQL", "Database", 3, "2026-01-01");
        sut.assignedSkillTable(List.of(skill));

        assertTrue(outContains("SQL"));
        assertTrue(outContains("3"));
        assertTrue(outContains("2026-01-01"));
    }

    @Test
    void testEmployeeCertificateTable() {
        AssignedCertificate cert = new AssignedCertificate(
                5, "Cloud Cert", "IT", "AWS", "2026-01-01", "2028-01-01", "C-99", "ACTIVE"
        );
        sut.employeeCertificateTable(List.of(cert));

        assertTrue(outContains("Cloud Cert"));
        assertTrue(outContains("C-99"));
        assertTrue(outContains("ACTIVE"));
        assertTrue(outContains("2028-01-01"));
    }

    @Test
    void testPrintStartupBanner() {
        sut.printStartupBanner();
        assertTrue(outContains("LuLuNiPhi 2.0"));
        assertTrue(outContains("\u001B[36m"));
    }
}
