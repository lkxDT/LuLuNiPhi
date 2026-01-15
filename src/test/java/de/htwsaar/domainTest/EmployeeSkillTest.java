package de.htwsaar.domainTest;

import de.htwsaar.domain.EmployeeSkill;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeSkillTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
        outContent.reset();
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("Test: Constructor and Getters for EmployeeSkill")
    void testEmployeeSkillCreation() {
        int expectedEmpId = 10;
        int expectedSkillId = 5;
        int expectedLevel = 3;
        String expectedDate = "2024-05-20";


        EmployeeSkill empSkill = new EmployeeSkill(expectedEmpId, expectedSkillId, expectedLevel, expectedDate);


        assertAll("Verify EmployeeSkill fields",
                () -> assertEquals(expectedEmpId, empSkill.getEmployeeId(), "Employee ID should match"),
                () -> assertEquals(expectedSkillId, empSkill.getSkillId(), "Skill ID should match"),
                () -> assertEquals(expectedLevel, empSkill.getLevel(), "Skill level should match"),
                () -> assertEquals(expectedDate, empSkill.getSinceDate(), "Since date should match")
        );

        System.out.println("\u001B[32m✔ EmployeeSkill Data Integrity Test Passed\u001B[0m");
    }

    @Test
    @DisplayName("Test: Level 0 (Below minimum) triggers notification")
    void testLevelBelowMin() {
        new EmployeeSkill(1, 1, 0, "2026-01-15");

        String output = outContent.toString();
        assertTrue(output.contains("1") && output.contains("5"),
                "\u001B[31m[FAIL] No notification for level 0!\u001B[0m");

        System.err.println("\u001B[32m✔ Level 0 blocked and notified.\u001B[0m");
    }

    @Test
    @DisplayName("Test: Negative level triggers notification")
    void testNegativeLevel() {
        new EmployeeSkill(1, 1, -5, "2026-01-15");

        String output = outContent.toString();
        assertTrue(output.contains("1") && output.contains("5"),
                "\u001B[31m[FAIL] No notification for negative level -5!\u001B[0m");

        System.err.println("\u001B[32m✔ Negative level blocked and notified.\u001B[0m");
    }

    @Test
    @DisplayName("Test: Level 99 (Above maximum) triggers notification")
    void testLevelAboveMax() {
        new EmployeeSkill(1, 1, 99, "2026-01-15");

        String output = outContent.toString();
        assertTrue(output.contains("1") && output.contains("5"),
                "\u001B[31m[FAIL] No notification for level 99!\u001B[0m");

        System.err.println("\u001B[32m✔ Level 99 blocked and notified.\u001B[0m");
    }
}
