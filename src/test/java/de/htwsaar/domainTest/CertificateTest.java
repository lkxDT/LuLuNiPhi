package de.htwsaar.domainTest;

import de.htwsaar.domain.Certificate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CertificateTest {

    @Test
    @DisplayName("Certificate: Constructor and Getters should correctly assign all values")
    void testCertificateCreation() {
        Certificate cert = new Certificate(
                1, "Oracle Certified Professional", "IT", "External",
                true, 24, "Professional Java certification"
        );

        assertAll("Verify all certificate fields",
                () -> assertEquals(1, cert.getId()),
                () -> assertEquals("Oracle Certified Professional", cert.getName()),
                () -> assertEquals("IT", cert.getCategory()),
                () -> assertEquals("External", cert.getIssuerType()),
                () -> assertTrue(cert.isRequiresRenewal()),
                () -> assertEquals(24, cert.getRenewalCycleMonths()),
                () -> assertEquals("Professional Java certification", cert.getDescription())
        );

        System.err.println("\u001B[32m✔ Certificate: Creation and full data integrity verified\u001B[0m");
    }

    @Test
    @DisplayName("Certificate: Should handle optional fields (null values) correctly")
    void testCertificateWithOptionalFieldsNull() {
        Certificate cert = new Certificate(
                2, "Internal Safety Briefing", "Safety", "Internal",
                false, null, null
        );

        assertAll("Verify optional fields are null",
                () -> assertFalse(cert.isRequiresRenewal()),
                () -> assertNull(cert.getRenewalCycleMonths(), "Renewal cycle should be null"),
                () -> assertNull(cert.getDescription(), "Description should be null")
        );

        System.err.println("\u001B[32m✔ Certificate: Correct handling of optional (null) fields verified\u001B[0m");
    }

    @Test
    @DisplayName("Certificate: toString should contain key data")
    void testToString() {
        Certificate cert = new Certificate(5, "TestCert", "Cat", "Type", false, null, "Desc");
        String output = cert.toString();

        assertAll("Check toString content",
                () -> assertTrue(output.contains("id=5")),
                () -> assertTrue(output.contains("name='TestCert'")),
                () -> assertTrue(output.contains("requiresRenewal=false")),
                () -> assertTrue(output.contains("description='Desc'"))
        );
        System.err.println("\u001B[32m✔ Certificate: toString output verified\u001B[0m");
    }
}