package de.htwsaar.communication.certificate;

import de.htwsaar.application.CertificateService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;
import de.htwsaar.domain.Certificate;
import de.htwsaar.MuteConsoleExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.easymock.EasyMock.*;

@ExtendWith(MuteConsoleExtension.class)
class CertificateAddMenuTest {

    private OutputRenderer outMock;
    private InputReader inMock;
    private CertificateService serviceMock;

    @BeforeEach
    void setUp() {
        outMock = createNiceMock(OutputRenderer.class);
        inMock = createMock(InputReader.class);
        serviceMock = createMock(CertificateService.class);
    }

    /**
     * Creates a dummy Certificate for Tests
     * @param id Certificate ID
     * @return   a Certificate object
     */
    private Certificate createTestCertificate(int id, boolean renewal) {
        return new Certificate(
                id, "Java Expert", "IT", "Oracle",
                renewal, renewal ? 12 : null, "Expert level certification"
        );
    }

    @Test
    void testAddCertificateWithRenewalSuccess() {
        Certificate cert = createTestCertificate(1,true);

        expect(inMock.readLine()).andReturn("New Cert");
        expect(inMock.readLine()).andReturn("IT");
        expect(inMock.readLine()).andReturn("Oracle");
        expect(inMock.readLine()).andReturn("y");
        expect(inMock.readInt("Invalid number.")).andReturn(12);
        expect(inMock.readLine()).andReturn("Description");

        expect(serviceMock.createCertificate("New Cert", "IT", "Oracle", true, 12, "Description"))
                .andReturn(cert);

        outMock.info("Certificate created.");
        expectLastCall().once();
        outMock.certificateTable(List.of(cert));
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        CertificateAddMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testAddCertificateWithoutRenewalSuccess() {
        Certificate cert = createTestCertificate(1,false);

        expect(inMock.readLine()).andReturn("Basic");
        expect(inMock.readLine()).andReturn("General");
        expect(inMock.readLine()).andReturn("Company");
        expect(inMock.readLine()).andReturn("n");
        expect(inMock.readLine()).andReturn("Desc");

        expect(serviceMock.createCertificate("Basic", "General", "Company", false, null, "Desc"))
                .andReturn(cert);

        outMock.info("Certificate created.");
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        CertificateAddMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testAddCertificateInvalidCycle() {
        expect(inMock.readLine()).andReturn("New Cert");
        expect(inMock.readLine()).andReturn("IT");
        expect(inMock.readLine()).andReturn("Oracle");
        expect(inMock.readLine()).andReturn("y");

        expect(inMock.readInt("Invalid number."))
                .andThrow(new IllegalArgumentException("Invalid number."));

        outMock.error("Invalid number.");
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        CertificateAddMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testAddCertificateServiceError() {
        expect(inMock.readLine()).andReturn("New Cert");
        expect(inMock.readLine()).andReturn("IT");
        expect(inMock.readLine()).andReturn("Oracle");
        expect(inMock.readLine()).andReturn("n");
        expect(inMock.readLine()).andReturn("Description");

        expect(serviceMock.createCertificate(anyString(), anyString(), anyString(), anyBoolean(), anyObject(), anyString()))
                .andThrow(new RuntimeException("Database error"));

        outMock.error("Database error");
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        CertificateAddMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }
}
