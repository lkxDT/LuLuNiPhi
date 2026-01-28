package de.htwsaar.repository;

import de.htwsaar.domain.AssignedCertificate;
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

import static org.junit.jupiter.api.Assertions.*;

class EmployeeCertificateRepositoryTest {

    private Connection connection;
    private DSLContext dsl;
    private EmployeeCertificateRepository sut;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        dsl = DSL.using(connection, SQLDialect.SQLITE);
        sut = new EmployeeCertificateRepository(dsl);

        dsl.execute("PRAGMA foreign_keys = ON;");
        createTables();
        insertBaseData();
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    /**
     * Creates the Tables (Employee, Certificate, Assignment) in the in-memory database.
     */
    private void createTables() {
        dsl.execute("CREATE TABLE employee (" +
                "id INTEGER PRIMARY KEY, " +
                "first_name TEXT, " +
                "last_name TEXT" +
                ")");

        dsl.execute("CREATE TABLE certificate (" +
                "id INTEGER PRIMARY KEY, " +
                "name TEXT, " +
                "category TEXT, " +
                "issuer_type TEXT" +
                ")");

        dsl.execute("CREATE TABLE employee_certificate (" +
                "employee_id INTEGER NOT NULL, " +
                "certificate_id INTEGER NOT NULL, " +
                "valid_from TEXT NOT NULL, " +
                "valid_until TEXT, " +
                "certificate_number TEXT, " +
                "status TEXT, " +
                "PRIMARY KEY (employee_id, certificate_id), " +
                "FOREIGN KEY (employee_id) REFERENCES employee(id), " +
                "FOREIGN KEY (certificate_id) REFERENCES certificate(id)" +
                ")");
    }

    /**
     * Fills the Tables with test Data
     */
    private void insertBaseData() {
        dsl.execute("INSERT INTO employee (id, first_name, last_name) VALUES (1, 'John', 'Doe')");
        dsl.execute("INSERT INTO certificate (id, name, category, issuer_type) VALUES (10, 'Java OCP', 'IT', 'Oracle')");
    }

    @Test
    void testAssignSuccess() {
        sut.assign(1, 10, "2026-01-01", "2028-01-01", "C1", "ACTIVE");

        assertTrue(sut.existsAssignment(1, 10));
    }

    @Test
    void testAssignDuplicateThrowsException() {
        sut.assign(1, 10, "2026-01-01", null, "C1", "ACTIVE");

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> sut.assign(1, 10, "2026-01-01", null, "C2", "ACTIVE")
        );

        assertTrue(exception.getMessage().contains("already assigned"));
    }

    @Test
    void testAssignInvalidForeignKeyThrowsException() {
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> sut.assign(99, 10, "2026-01-01", null, null, "ACTIVE")
        );

        assertTrue(exception.getMessage().contains("foreign key constraint failed"));
    }

    @Test
    void testExistsAssignment() {
        assertFalse(sut.existsAssignment(1, 10));

        sut.assign(1, 10, "2026-01-01", null, null, "ACTIVE");

        assertTrue(sut.existsAssignment(1, 10));
    }

    @Test
    void testFindByEmployeeId() {
        sut.assign(1, 10, "2026-01-01", "2028-01-01", "C1", "ACTIVE");

        List<AssignedCertificate> result = sut.findByEmployeeId(1);

        assertEquals(1, result.size());
        AssignedCertificate ac = result.get(0);
        assertEquals(10, ac.getCertificateId());
        assertEquals("Java OCP", ac.getName());
        assertEquals("IT", ac.getCategory());
        assertEquals("2026-01-01", ac.getValidFrom());
        assertEquals("C1", ac.getCertificateNumber());
        assertEquals("ACTIVE", ac.getStatus());
    }

    @Test
    void testFindByEmployeeIdReturnsEmpty() {
        List<AssignedCertificate> result = sut.findByEmployeeId(1);
        assertTrue(result.isEmpty());
    }

    @Test
    void testUpdateAssignment() {
        sut.assign(1, 10, "2026-01-01", null, "OLD-NUM", "PENDING");

        sut.updateAssignment(1, 10, "2026-02-01", "2028-01-01", "NEW-NUM", "ACTIVE");

        List<AssignedCertificate> result = sut.findByEmployeeId(1);
        AssignedCertificate updated = result.get(0);

        assertEquals("2026-02-01", updated.getValidFrom());
        assertEquals("2028-01-01", updated.getValidUntil());
        assertEquals("NEW-NUM", updated.getCertificateNumber());
        assertEquals("ACTIVE", updated.getStatus());
    }

    @Test
    void testUpdateAssignmentThrowsIfNotFound() {
        assertThrows(
                IllegalStateException.class,
                () -> sut.updateAssignment(1, 10, "2026-01-01", null, null, "ACTIVE")
        );
    }

    @Test
    void testRemoveAssignment() {
        sut.assign(1, 10, "2026-01-01", null, null, "ACTIVE");
        assertTrue(sut.existsAssignment(1, 10));

        sut.removeAssignment(1, 10);

        assertFalse(sut.existsAssignment(1, 10));
    }

    @Test
    void testRemoveAssignmentThrowsIfNotFound() {
        assertThrows(
                IllegalStateException.class,
                () -> sut.removeAssignment(1, 10)
        );
    }
}