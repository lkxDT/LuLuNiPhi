package de.htwsaar.repository;

import de.htwsaar.domain.Employee;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeRepositoryTest {

    private Connection connection;
    private DSLContext dsl;
    private EmployeeRepository sut;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        dsl = DSL.using(connection, SQLDialect.SQLITE);

        createTables();

        sut = new EmployeeRepository(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    /**
     * Creates the  Tables (Employee, Skill, Certificate) in the in-memory database.
     */
    private void createTables() {
        dsl.execute("CREATE TABLE employee (" +
                "id INTEGER PRIMARY KEY, " +
                "first_name TEXT, " +
                "last_name TEXT, " +
                "mail TEXT, " +
                "role TEXT, " +
                "status TEXT, " +
                "hire_date TEXT, " +
                "working_hours REAL" +
                ")");

        dsl.execute("CREATE TABLE employee_skill (" +
                "employee_id INTEGER, " +
                "skill_id INTEGER, " +
                "FOREIGN KEY (employee_id) REFERENCES employee(id)" +
                ")");

        dsl.execute("CREATE TABLE employee_certificate (" +
                "employee_id INTEGER, " +
                "certificate_id INTEGER, " +
                "FOREIGN KEY (employee_id) REFERENCES employee(id)" +
                ")");
    }

    @Test
    void testSaveSuccess() {
        Employee saved = sut.save("John", "Doe", "john.doe@test.com", "Dev", "Active", "2026-01-01", 40);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("John", saved.getFirstName());
        assertEquals("Doe", saved.getLastName());
        assertEquals("2026-01-01", saved.getHireDate());

        assertTrue(sut.findByID(saved.getId()).isPresent());
    }

    @Test
    void testFindByIdSuccess() {
        dsl.execute("INSERT INTO employee (first_name, last_name, mail, role, status, hire_date, working_hours) " +
                "VALUES ('John', 'Doe', 'john.doe@test.com', 'Dev', 'Active', '2026-01-01', 40)");
        int id = dsl.lastID().intValue();

        Optional<Employee> result = sut.findByID(id);

        assertTrue(result.isPresent());
        assertEquals("John", result.get().getFirstName());
    }

    @Test
    void testFindByIdReturnsEmpty() {
        Optional<Employee> result = sut.findByID(999);

        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByLastName() {
        // Updated INSERTs to include working_hours
        dsl.execute("INSERT INTO employee (first_name, last_name, mail, working_hours) VALUES ('A', 'Doe', 'a@t.com', 40)");
        dsl.execute("INSERT INTO employee (first_name, last_name, mail, working_hours) VALUES ('B', 'Doer', 'b@t.com', 40)");
        dsl.execute("INSERT INTO employee (first_name, last_name, mail, working_hours) VALUES ('C', 'Smith', 'c@t.com', 40)");

        List<Employee> result = sut.findByLastName("do");

        assertEquals(2, result.size());
        assertEquals("Doe", result.get(0).getLastName());
        assertEquals("Doer", result.get(1).getLastName());
    }

    @Test
    void testFindAll() {
        // Updated INSERTs to include working_hours
        dsl.execute("INSERT INTO employee (first_name, last_name, working_hours) VALUES ('A', 'A', 40)");
        dsl.execute("INSERT INTO employee (first_name, last_name, working_hours) VALUES ('B', 'B', 40)");

        List<Employee> result = sut.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void testExistsByEmail() {
        dsl.execute("INSERT INTO employee (first_name, mail, working_hours) VALUES ('John', 'john.doe@test.com', 40)");

        assertTrue(sut.existsByEmail("john.doe@test.com"));
        assertFalse(sut.existsByEmail("other@test.com"));
    }

    @Test
    void testExistsByEmailForOtherEmployee() {
        dsl.execute("INSERT INTO employee (id, first_name, mail, working_hours) VALUES (1, 'John', 'john.doe@test.com', 40)");
        dsl.execute("INSERT INTO employee (id, first_name, mail, working_hours) VALUES (2, 'Jane', 'unique@test.com', 40)");

        assertTrue(sut.existsByEmailForOtherEmployee("john.doe@test.com", 2));
        assertFalse(sut.existsByEmailForOtherEmployee("john.doe@test.com", 1));
    }

    @Test
    void testUpdate() {
        Employee original = sut.save("Old", "Name", "old@test.com", "Dev", "Active", "2026-01-01", 40);

        Employee toUpdate = new Employee(
                original.getId(), "New", "Name", "new@test.com", "Lead", "Inactive", "2026-02-01", 30
        );

        sut.update(toUpdate);

        Optional<Employee> loaded = sut.findByID(original.getId());
        assertTrue(loaded.isPresent());
        assertEquals("New", loaded.get().getFirstName());
        assertEquals("new@test.com", loaded.get().getEmail());
        assertEquals("Lead", loaded.get().getRole());
        assertEquals(30, loaded.get().getWorkingHours());
    }

    @Test
    void testDeleteByIdRemovesEmployeeAndDependencies() {
        dsl.execute("INSERT INTO employee (id, first_name, working_hours) VALUES (1, 'John', 40)");
        dsl.execute("INSERT INTO employee_skill (employee_id, skill_id) VALUES (1, 100)");
        dsl.execute("INSERT INTO employee_certificate (employee_id, certificate_id) VALUES (1, 200)");

        boolean deleted = sut.deleteById(1);

        assertTrue(deleted);
        assertTrue(sut.findByID(1).isEmpty());

        int skills = dsl.fetch("SELECT * FROM employee_skill WHERE employee_id = 1").size();
        int certs = dsl.fetch("SELECT * FROM employee_certificate WHERE employee_id = 1").size();
        assertEquals(0, skills);
        assertEquals(0, certs);
    }

    @Test
    void testDeleteByIdReturnsFalseIfNotFound() {
        boolean deleted = sut.deleteById(999);

        assertFalse(deleted);
    }
}
