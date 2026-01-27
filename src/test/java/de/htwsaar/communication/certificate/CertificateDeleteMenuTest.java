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
import java.util.List;

import static org.easymock.EasyMock.*;

@ExtendWith(MuteConsoleExtension.class)
class CertificateDeleteMenuTest {

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
    private Certificate createTestCertificate(int id) {
        return new Certificate(
                id, "Java Expert", "IT", "Oracle",
                false, null, "Expert level certification"
        );
    }

    @Test
    void testDeleteSuccess() {
        int id = 1;
        Certificate cert = createTestCertificate(id);

        expect(inMock.readInt("Invalid ID.")).andReturn(id);
        expect(serviceMock.findCertificateById(id)).andReturn(Optional.of(cert));

        outMock.certificateTable(List.of(cert));
        expectLastCall().once();

        expect(inMock.readLine()).andReturn("y");

        serviceMock.deleteCertificateById(id);
        expectLastCall().once();

        outMock.info("Certificate deleted.");
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        CertificateDeleteMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testDeleteCancelled() {
        int id = 1;
        Certificate cert = createTestCertificate(id);

        expect(inMock.readInt("Invalid ID.")).andReturn(id);
        expect(serviceMock.findCertificateById(id)).andReturn(Optional.of(cert));

        outMock.certificateTable(List.of(cert));
        expectLastCall().once();

        expect(inMock.readLine()).andReturn("n");

        outMock.info("Delete canceled.");
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        CertificateDeleteMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testInvalidIDInput() {
        expect(inMock.readInt("Invalid ID.")).andThrow(new IllegalArgumentException("Invalid ID."));

        outMock.error("Invalid ID.");
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        CertificateDeleteMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testCertificateNotFound() {
        int id = 99;
        expect(inMock.readInt("Invalid ID.")).andReturn(id);
        expect(serviceMock.findCertificateById(id)).andReturn(Optional.empty());

        outMock.error("Certificate with ID " + id + " not found.");
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        CertificateDeleteMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testDeleteServiceError() {
        int id = 1;
        Certificate cert = createTestCertificate(id);

        expect(inMock.readInt("Invalid ID.")).andReturn(id);
        expect(serviceMock.findCertificateById(id)).andReturn(Optional.of(cert));

        outMock.certificateTable(List.of(cert));
        expectLastCall().once();

        expect(inMock.readLine()).andReturn("y");

        serviceMock.deleteCertificateById(id);
        expectLastCall().andThrow(new RuntimeException("Database error"));

        outMock.error("Database error");
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        CertificateDeleteMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }
}
