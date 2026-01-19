package de.htwsaar.applicationtests;

import de.htwsaar.application.CertificateService;
import de.htwsaar.domain.Certificate;
import de.htwsaar.repositoryTest.MockCertificateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CertificateServiceTest {

    private CertificateService certificateService;
    private MockCertificateRepository mockRepo;

    @BeforeEach
    void setUp() {
        this.mockRepo = new MockCertificateRepository();
        this.certificateService = new CertificateService(mockRepo);
    }

    // --- HAPPY PATH TESTS ---

    @Test
    @DisplayName("Should create valid certificate without renewal")
    void testCreateCertificateSuccessNoRenewal() {
        Certificate cert = certificateService.createCertificate(
                " Java Foundation ", "Dev", "Oracle", false, 0, "Desc"
        );

        assertNotNull(cert);
        assertEquals("Java Foundation", cert.getName());
        assertNull(cert.getRenewalCycleMonths());
    }

    @Test
    @DisplayName("Should create valid certificate with renewal")
    void testCreateCertificateSuccessWithRenewal() {
        Certificate cert = certificateService.createCertificate(
                "First Aid", "Safety", "Red Cross", true, 24, "Valid for 2 years"
        );

        assertTrue(cert.isRequiresRenewal());
        assertEquals(24, cert.getRenewalCycleMonths());
    }

    // --- NEGATIVE / VALIDATION TESTS ---

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   "})
    @DisplayName("Should throw exception for blank names")
    void testCreateCertificateInvalidNames(String invalidName) {
        assertThrows(IllegalArgumentException.class, () ->
                certificateService.createCertificate(invalidName, "Cat", "Issuer", false, null, null)
        );
    }

    @Test
    @DisplayName("Should throw exception if renewal required but cycle is null or zero")
    void testRenewalValidation() {
        assertThrows(IllegalArgumentException.class, () ->
                certificateService.createCertificate("Test", "Cat", "Iss", true, null, null)
        );
        assertThrows(IllegalArgumentException.class, () ->
                certificateService.createCertificate("Test", "Cat", "Iss", true, 0, null)
        );
    }

    // --- DELEGATION TESTS ---

    @Test
    @DisplayName("Should delegate findById to repository")
    void testFindById() {
        certificateService.createCertificate("OCP", "IT", "Oracle", false, null, null);
        Optional<Certificate> found = certificateService.findCertificateById(1);

        assertTrue(found.isPresent());
        assertEquals("OCP", found.get().getName());
    }
}
