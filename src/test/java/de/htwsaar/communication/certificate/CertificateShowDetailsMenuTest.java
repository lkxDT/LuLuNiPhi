package de.htwsaar.communication.certificate;

import de.htwsaar.application.CertificateService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;
import de.htwsaar.domain.Certificate;
import de.htwsaar.MuteConsoleExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Optional;

import static org.easymock.EasyMock.*;

@ExtendWith(MuteConsoleExtension.class)
class CertificateShowDetailsMenuTest {

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
     * @param id       Certificate ID
     * @param renewal  Boolean if renewal is required
     * @return         a Certificate object
     */
    private Certificate createTestCertificate(int id, boolean renewal) {
        return new Certificate(
                id, "Java Expert", "IT", "Oracle",
                renewal, renewal ? 12 : null, "Expert level certification"
        );
    }

    @Test
    void testShowDetailsSuccess() {
        int id = 10;
        Certificate cert = createTestCertificate(id, true);

        expect(inMock.readInt("Invalid ID.")).andReturn(id);
        expect(serviceMock.findCertificateById(id)).andReturn(Optional.of(cert));

        replay(outMock, inMock, serviceMock);
        CertificateShowDetailsMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testShowDetailsSuccessNoRenewalAndNoDescription() {
        int id = 20;
        Certificate cert = createTestCertificate(id, false);

        expect(inMock.readInt("Invalid ID.")).andReturn(id);
        expect(serviceMock.findCertificateById(id)).andReturn(Optional.of(cert));

        replay(outMock, inMock, serviceMock);
        CertificateShowDetailsMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testCertificateNotFound() {
        int id = 99;
        expect(inMock.readInt("Invalid ID.")).andReturn(id);
        expect(serviceMock.findCertificateById(id)).andReturn(Optional.empty());

        outMock.error("Certificate not found.");
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        CertificateShowDetailsMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testInvalidIDInput() {
        expect(inMock.readInt("Invalid ID.")).andThrow(new IllegalArgumentException("Invalid ID."));

        outMock.error("Invalid ID.");
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        CertificateShowDetailsMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }
}