package de.htwsaar.repository;

import de.htwsaar.domain.Skill;
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

class SkillRepositoryTest {

    private Connection connection;
    private DSLContext dsl;
    private SkillRepository sut;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        dsl = DSL.using(connection, SQLDialect.SQLITE);
        sut = new SkillRepository(connection);

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
     * Creates the Skill table in the in-memory database.
     */
    private void createTables() {
        dsl.execute("CREATE TABLE skill (" +
                "id INTEGER PRIMARY KEY, " +
                "skill_name TEXT, " +
                "category TEXT" +
                ")");
    }

    /**
     * Inserts default skills for testing purposes.
     */
    private void insertBaseData() {
        dsl.execute("INSERT INTO skill (id, skill_name, category) VALUES (1, 'Java', 'Programming')");
        dsl.execute("INSERT INTO skill (id, skill_name, category) VALUES (2, 'Project Management', 'Soft Skills')");
    }

    @Test
    void testSaveSuccess() {
        Skill saved = sut.save("Python", "Programming");

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("Python", saved.getName());
        assertEquals("Programming", saved.getCategory());

        assertTrue(sut.findByID(saved.getId()).isPresent());
    }

    @Test
    void testFindByIdSuccess() {
        Optional<Skill> result = sut.findByID(1);

        assertTrue(result.isPresent());
        assertEquals("Java", result.get().getName());
        assertEquals("Programming", result.get().getCategory());
    }

    @Test
    void testFindByIdReturnsEmpty() {
        Optional<Skill> result = sut.findByID(999);

        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByNameSuccess() {
        Optional<Skill> result = sut.findByName("Java");

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
    }

    @Test
    void testFindByNameReturnsEmpty() {
        Optional<Skill> result = sut.findByName("NonExistent");

        assertTrue(result.isEmpty());
    }

    @Test
    void testFindAll() {
        List<Skill> result = sut.findAll();

        assertEquals(2, result.size());
        assertEquals("Java", result.get(0).getName());
        assertEquals("Project Management", result.get(1).getName());
    }

    @Test
    void testUpdate() {
        Skill existing = sut.findByID(1).orElseThrow();

        Skill toUpdate = new Skill(existing.getId(), "Java Advanced", "Backend");

        sut.update(toUpdate);

        Skill updated = sut.findByID(1).orElseThrow();
        assertEquals("Java Advanced", updated.getName());
        assertEquals("Backend", updated.getCategory());
    }

    @Test
    void testDeleteByIdSuccess() {
        boolean deleted = sut.deleteById(1);

        assertTrue(deleted);
        assertTrue(sut.findByID(1).isEmpty());
    }

    @Test
    void testDeleteByIdReturnsFalseIfNotFound() {
        boolean deleted = sut.deleteById(999);

        assertFalse(deleted);
    }

    @Test
    void testFindSkillsByName() {
        dsl.execute("INSERT INTO skill (skill_name, category) VALUES ('JavaScript', 'Web')");

        List<Skill> result = sut.findSkillsByName("Java");

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(s -> s.getName().equals("Java")));
        assertTrue(result.stream().anyMatch(s -> s.getName().equals("JavaScript")));
    }
}
