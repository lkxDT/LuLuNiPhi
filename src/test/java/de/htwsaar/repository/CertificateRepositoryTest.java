package de.htwsaar.repository;

import de.htwsaar.domain.Certificate;
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

class CertificateRepositoryTest {

    private Connection connection;
    private DSLContext dsl;
    private CertificateRepository sut;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        dsl = DSL.using(connection, SQLDialect.SQLITE);
        sut = new CertificateRepository(dsl);

        createTable();
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    /**
     * Creates a Table in the in-memory database for testing.
     */
    private void createTable() {
        dsl.execute("CREATE TABLE certificate (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "category TEXT NOT NULL, " +
                "issuer_type TEXT NOT NULL, " +
                "requires_renewal INTEGER NOT NULL, " +
                "renewal_cycle_months INTEGER, " +
                "description TEXT" +
                ")");
    }

    @Test
    void testSaveAndFindById() {
        Certificate saved = sut.save(
                "Java OCP",
                "Programming",
                "Oracle",
                true,
                24,
                "Professional Developer"
        );

        assertNotNull(saved);
        assertTrue(saved.getId() > 0);

        Optional<Certificate> found = sut.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Java OCP", found.get().getName());
        assertTrue(found.get().isRequiresRenewal());
        assertEquals(24, found.get().getRenewalCycleMonths());
    }

    @Test
    void testSaveHandlesBooleanMappingCorrectly() {
        Certificate certFalse = sut.save("Java OCP", "Programming", "Oracle", false, null, "Professional Developer");
        Optional<Certificate> resultFalse = sut.findById(certFalse.getId());
        assertTrue(resultFalse.isPresent());
        assertFalse(resultFalse.get().isRequiresRenewal());

        Certificate certTrue = sut.save("Java OCP", "Programming", "Oracle", true, 24, "Professional Developer");
        Optional<Certificate> resultTrue = sut.findById(certTrue.getId());
        assertTrue(resultTrue.isPresent());
        assertTrue(resultTrue.get().isRequiresRenewal());
    }

    @Test
    void testFindByName() {
        sut.save("Java OCP", "Programming", "Oracle", false, null, "Professional Developer");

        Optional<Certificate> result = sut.findByName("Java OCP");
        assertTrue(result.isPresent());
        assertEquals("Java OCP", result.get().getName());

        Optional<Certificate> notFound = sut.findByName("NonExistent");
        assertTrue(notFound.isEmpty());
    }

    @Test
    void testExistsByNameAndIssuer() {
        sut.save("Cert A", "Programming", "Issuer X", false, null, "Professional Developer");

        assertTrue(sut.existsByNameAndIssuer("Cert A", "Issuer X"));
        assertFalse(sut.existsByNameAndIssuer("Cert A", "Issuer Y"));
        assertFalse(sut.existsByNameAndIssuer("Cert B", "Issuer X"));
    }

    @Test
    void testFindAll() {
        sut.save("Cert A", "Programming", "Issuer X", false, null, "Professional Developer");
        sut.save("Cert B", "Programming", "Issuer Y", false, null, "Professional Developer");

        List<Certificate> all = sut.findAll();
        assertEquals(2, all.size());
    }

    @Test
    void testUpdate() {
        Certificate initial = sut.save("Java OCP", "Programming", "Oracle", false, null, "Professional Developer");

        Certificate toUpdate = new Certificate(
                initial.getId(),
                "New Name",
                "New Cat",
                "New Iss",
                true,
                12,
                "New Desc"
        );

        sut.update(toUpdate);

        Optional<Certificate> updated = sut.findById(initial.getId());
        assertTrue(updated.isPresent());
        assertEquals("New Name", updated.get().getName());
        assertEquals("New Cat", updated.get().getCategory());
        assertTrue(updated.get().isRequiresRenewal());
        assertEquals(12, updated.get().getRenewalCycleMonths());
    }

    @Test
    void testDeleteById() {
        Certificate cert = sut.save("Java OCP", "Programming", "Oracle", false, null, "Professional Developer");
        int id = cert.getId();

        boolean deleted = sut.deleteById(id);
        assertTrue(deleted);

        Optional<Certificate> lookup = sut.findById(id);
        assertTrue(lookup.isEmpty());
    }

    @Test
    void testDeleteByIdReturnsFalseForNonExistentId() {
        boolean deleted = sut.deleteById(999);
        assertFalse(deleted);
    }
}
