package de.htwsaar.applicationtests;

import de.htwsaar.application.EmployeeService;
import de.htwsaar.domain.Employee;
import de.htwsaar.repository.EmployeeRepository;
import de.htwsaar.repositoryTest.MockEmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeServiceTest {

    private EmployeeService employeeService;
    private EmployeeRepository mockRepo;

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
                "Clara", "Hahn", "clara.hahn@firma.de",
                "Researcher", "Active", "2018-10-27", 20
        );
        assertNotNull(created);
        assertEquals("Clara", created.getFirstName());
        assertEquals(3, created.getID(), "New employee should get ID 3");
        System.err.println("\u001B[32m✔ Service handled creation with dynamic ID assignment\u001B[0m");
    }
    @Test
    @DisplayName("Should return an empty list when no employees exist")
    void testEmptyRepository() {
        EmployeeService serviceWithNoData = new EmployeeService(new EmployeeRepository(null) {
            @Override
            public List<Employee> findAll() {
                return new java.util.ArrayList<>();
            }
        });
        List<Employee> result = serviceWithNoData.findAllEmployees();
        assertNotNull(result, "Service should return an empty list, never null");
        assertEquals(0, result.size());
        System.err.println("\u001B[32m✔ Correctly handled empty database state\u001B[0m");
    }
    @Test
    @DisplayName("Service: Should throw Exception when hours exceed 168")
    void testCreateEmployeeThrowsExceptionForTooManyHours() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            employeeService.createEmployee("Max", "Power", "max@test.de", "Dev", "Active", "2026-01-13", 200);
        });
        assertTrue(exception.getMessage().contains("Working hours must"), "Exception message should mention the hour limit");
        System.err.println("\u001B[32m✔ Service correctly threw IllegalArgumentException\u001B[0m");
    }
    @Test
    @DisplayName("Should handle null inputs gracefully")
    void testNullInputs() {
        assertThrows(RuntimeException.class, () -> {
            employeeService.createEmployee(null, null, null, null, null, null, 0);
        }, "Service should throw an exception if mandatory database fields are null");
        System.err.println("\u001B[32m✔ Service correctly rejected/passed null error\u001B[0m");
    }
}