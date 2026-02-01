package de.htwsaar.applicationTests;

import de.htwsaar.application.EmployeeService;
import de.htwsaar.domain.Employee;
import de.htwsaar.MockRepository.MockEmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeServiceTest {
    private EmployeeService employeeService;
    private MockEmployeeRepository mockRepo;

    @BeforeEach
    void setUp() {
        this.mockRepo = new MockEmployeeRepository();
        this.employeeService = new EmployeeService(mockRepo);
    }


    @Test
    @DisplayName("getAllEmployees should return the full list from the repository")
    void testGetAllEmployees() {
        List<Employee> employees = employeeService.findAllEmployees();
        assertNotNull(employees, "\u001B[31mEmployee list should not be null\u001B[0m");
        assertEquals(2, employees.size(), "MockRepo should return 2 employees");
        assertEquals("Mia", employees.get(0).getFirstName());
        assertEquals("Ben", employees.get(1).getFirstName());
        System.err.println("\u001B[32m✔ Service correctly retrieved employees from MockRepo\u001B[0m");
    }

    @Test
    @DisplayName("createEmployee should successfully pass data to repository and return employee")
    void testCreateEmployee() {
        Employee created = employeeService.createEmployee(
                "Clara", "Hahn",
                "Researcher", "ACTIVE", "2018-10-27", 20
        );
        assertNotNull(created);
        assertEquals("Clara", created.getFirstName());
        assertEquals(3, created.getId(), "New employee should get ID 3");
        assertEquals("clara.hahn@medtech.de", created.getEmail());
        System.err.println("\u001B[32m✔ Service handled creation with dynamic ID assignment\u001B[0m");
    }

    @Test
    @DisplayName("Should return an empty list when the repository is cleared")
    void testEmptyRepository() {
        mockRepo.clear();
        List<Employee> result = employeeService.findAllEmployees();
        assertNotNull(result);
        assertEquals(0, result.size(), "List should be empty after clearing the repo");
        System.err.println("\u001B[32m✔ Correctly handled empty database state after clear()\u001B[0m");
    }

    @Test
    @DisplayName("Service: Should throw Exception when hours exceed 60")
    void testCreateEmployeeThrowsExceptionForTooManyHours() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            employeeService.createEmployee("Max", "Power", "Dev", "ACTIVE", "2026-01-13", 61);
        });
        assertEquals("Working hours must not exceed 60 per week.", exception.getMessage());
        System.err.println("\u001B[32m✔ Service correctly threw IllegalArgumentException for excessive hours\u001B[0m");
    }

    @ParameterizedTest
    @CsvSource({
            "null, lastName",
            "firstName, ''",
            "' ', lastName"
    })
    @DisplayName("Validation: Should reject null or blank mandatory fields")
    void testInvalidInputs(String fName, String lName) {
        String first = "null".equals(fName) ? null : fName;
        String last = "null".equals(lName) ? null : lName;

        assertThrows(IllegalArgumentException.class, () ->
                employeeService.createEmployee(first, last, "Dev", "ACTIVE", "2026-01-19", 40)
        );
        System.err.println("\u001B[32m✔ Service correctly rejected invalid name: " + fName + " " + lName + "\u001B[0m");
    }

    @Test
    @DisplayName("Email Logic: Should normalize German umlauts (ä,ö,ü,ß) and remove spaces")
    void testEmailNormalization() {
        Employee created = employeeService.createEmployee(
                " Jürgen ", " Müller ", "Dev", "ACTIVE", "2026-01-19", 20
        );

        assertEquals("juergen.mueller@medtech.de", created.getEmail());
        System.err.println("\u001B[32m✔ Service correctly normalized German umlauts and trimmed spaces\u001B[0m");
    }

    @Test
    @DisplayName("Email Logic: Should increment counter for duplicate names")
    void testDuplicateEmailHandling() {
        employeeService.createEmployee("Max", "Mustermann", "Dev", "ACTIVE", "2026-01-19", 40);

        Employee second = employeeService.createEmployee("Max", "Mustermann", "Dev", "ACTIVE", "2026-01-19", 40);
        assertEquals("max.mustermann2@medtech.de", second.getEmail());

        Employee third = employeeService.createEmployee("Max", "Mustermann", "Dev", "ACTIVE", "2026-01-19", 40);
        assertEquals("max.mustermann3@medtech.de", third.getEmail());
        System.err.println("\u001B[32m✔ Service correctly incremented email counter for duplicate names\u001B[0m");
    }

    @Test
    @DisplayName("Search: Should find employees by last name")
    void testFindByLastName() {
        employeeService.createEmployee("John", "Doe", "Dev", "ACTIVE", "2026-01-19", 40);
        List<Employee> results = employeeService.findEmployeesByLastName("Doe");
        assertFalse(results.isEmpty());
        assertEquals("John", results.get(0).getFirstName());
        System.err.println("\u001B[32m✔ Service correctly retrieved employees by last name\u001B[0m");
    }
}