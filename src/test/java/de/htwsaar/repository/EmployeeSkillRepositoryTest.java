package de.htwsaar.repository;

import de.htwsaar.domain.AssignedSkill;
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

class EmployeeSkillRepositoryTest {

    private Connection connection;
    private DSLContext dsl;
    private EmployeeSkillRepository sut;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        dsl = DSL.using(connection, SQLDialect.SQLITE);
        sut = new EmployeeSkillRepository(dsl);

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
     * Creates the Tables (employee, skill, employee_skill) in the in-memory database.
     */
    private void createTables() {
        dsl.execute("CREATE TABLE employee (" +
                "id INTEGER PRIMARY KEY, " +
                "first_name TEXT, " +
                "last_name TEXT" +
                ")");

        dsl.execute("CREATE TABLE skill (" +
                "id INTEGER PRIMARY KEY, " +
                "skill_name TEXT, " +
                "category TEXT" +
                ")");

        dsl.execute("CREATE TABLE employee_skill (" +
                "employee_id INTEGER NOT NULL, " +
                "skill_id INTEGER NOT NULL, " +
                "level INTEGER, " +
                "since_date TEXT, " +
                "PRIMARY KEY (employee_id, skill_id), " +
                "FOREIGN KEY (employee_id) REFERENCES employee(id), " +
                "FOREIGN KEY (skill_id) REFERENCES skill(id)" +
                ")");
    }

    /**
     * Fills the Tables with test Data
     */
    private void insertBaseData() {
        dsl.execute("INSERT INTO employee (id, first_name, last_name) VALUES (1, 'John', 'Doe')");
        dsl.execute("INSERT INTO skill (id, skill_name, category) VALUES (50, 'Java', 'Programming')");
    }

    @Test
    void testAssignSuccess() {
        sut.assign(1, 50, 2);

        assertTrue(sut.existsAssignment(1, 50));
    }

    @Test
    void testAssignDuplicateThrowsException() {
        sut.assign(1, 50, 1);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> sut.assign(1, 50, 2)
        );

        assertTrue(exception.getMessage().contains("already assigned"));
    }

    @Test
    void testAssignInvalidForeignKeyThrowsException() {
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> sut.assign(99, 50, 1)
        );

        assertTrue(exception.getMessage().contains("foreign key constraint failed"));
    }

    @Test
    void testExistsAssignment() {
        assertFalse(sut.existsAssignment(1, 50));

        sut.assign(1, 50, 1);

        assertTrue(sut.existsAssignment(1, 50));
    }

    @Test
    void testFindByEmployeeId() {
        sut.assign(1, 50, 2);

        List<AssignedSkill> result = sut.findByEmployeeId(1);

        assertEquals(1, result.size());
        AssignedSkill as = result.get(0);

        assertEquals(50, as.getId());
        assertEquals("Java", as.getName());
        assertEquals("Programming", as.getCategory());
        assertEquals(2, as.getLevel());
    }

    @Test
    void testFindByEmployeeIdReturnsEmpty() {
        List<AssignedSkill> result = sut.findByEmployeeId(1);
        assertTrue(result.isEmpty());
    }

    @Test
    void testUpdateAssignment() {
        sut.assign(1, 50, 1);

        sut.updateAssignment(1, 50, 4);

        List<AssignedSkill> result = sut.findByEmployeeId(1);
        AssignedSkill updated = result.get(0);

        assertEquals(4, updated.getLevel());
    }

    @Test
    void testUpdateAssignmentThrowsIfNotFound() {
        assertThrows(
                IllegalStateException.class,
                () -> sut.updateAssignment(1, 50, 2)
        );
    }

    @Test
    void testRemoveAssignment() {
        sut.assign(1, 50, 1);
        assertTrue(sut.existsAssignment(1, 50));

        sut.removeAssignment(1, 50);

        assertFalse(sut.existsAssignment(1, 50));
    }

    @Test
    void testRemoveAssignmentThrowsIfNotFound() {
        assertThrows(
                IllegalStateException.class,
                () -> sut.removeAssignment(1, 50)
        );
    }
}