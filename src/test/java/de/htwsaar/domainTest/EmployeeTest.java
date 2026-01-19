package de.htwsaar.domainTest;

import de.htwsaar.domain.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EmployeeTest {

    @Test
    @DisplayName("Test: Constructor and Getters")
    void testEmployeeCreation() {
        Employee emp = new Employee(1, "Clara", "Hahn", "clara@test.de", "Dev", "Active", "2026-01-15", 40);

        assertAll("Verify all employee fields",
                () -> assertEquals(1, emp.getId()),
                () -> assertEquals("Clara", emp.getFirstName()),
                () -> assertEquals("Hahn", emp.getLastName()),
                () -> assertEquals("clara@test.de", emp.getEmail()),
                () -> assertEquals("Dev", emp.getRole()),
                () -> assertEquals("Active", emp.getStatus()),
                () -> assertEquals("2026-01-15", emp.getHireDate()),
                () -> assertEquals(40, emp.getWorkingHours())
        );

        System.out.println("\u001B[32m✔ Employee Creation Test Passed\u001B[0m");
    }


    @Test
    @DisplayName("Test: toString Formatting")
    void testToStringFormatting() {
        Employee emp = new Employee(99, "Max", "Mustermann", "max@web.de", "Admin", "Active", "2020-01-01", 38);

        String output = emp.toString();

        assertTrue(output.contains("99"), "ID missing in toString");
        assertTrue(output.contains("Max"), "First name missing in toString");
        assertTrue(output.contains("38"), "Working hours missing in toString");

        assertTrue(output.startsWith("   99"), "ID formatting (padding) is incorrect");

        System.out.println("\u001B[32m✔ Employee toString Formatting Test Passed\u001B[0m");
    }
}
