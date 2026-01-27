package de.htwsaar.communication.certificate;

import de.htwsaar.application.CertificateService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;
import de.htwsaar.MuteConsoleExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Collections;

import static org.easymock.EasyMock.*;

@ExtendWith(MuteConsoleExtension.class)
class CertificateMenuTest {

    private OutputRenderer outMock;
    private InputReader inMock;
    private CertificateService serviceMock;
    private CertificateMenu sut;

    @BeforeEach
    void setUp() {
        outMock = createNiceMock(OutputRenderer.class);
        inMock = createMock(InputReader.class);
        serviceMock = createMock(CertificateService.class);
        sut = new CertificateMenu(serviceMock, inMock, outMock);
    }

    @Test
    void testShowAll() {
        expect(inMock.readMenuNumber(6)).andReturn(1);
        expect(serviceMock.findAllCertificates()).andReturn(Collections.emptyList());

        outMock.certificateTable(Collections.emptyList());
        expectLastCall().once();

        expect(inMock.readMenuNumber(6)).andReturn(6);

        replay(outMock, inMock, serviceMock);
        sut.show();
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testReturnImmediately() {
        expect(inMock.readMenuNumber(6)).andReturn(6);

        replay(outMock, inMock, serviceMock);
        sut.show();
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testNavigateToShowDetails() {
        expect(inMock.readMenuNumber(6)).andReturn(2);
        expect(inMock.readInt("Invalid ID.")).andThrow(new IllegalArgumentException("Invalid ID."));

        outMock.error("Invalid ID.");
        expectLastCall().once();

        expect(inMock.readMenuNumber(6)).andReturn(6);

        replay(outMock, inMock, serviceMock);
        sut.show();
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testNavigateToAddMenu() {
        expect(inMock.readMenuNumber(6)).andReturn(3);

        expect(inMock.readLine()).andReturn("New Cert");
        expect(inMock.readLine()).andReturn("Category");
        expect(inMock.readLine()).andReturn("Issuer");
        expect(inMock.readLine()).andReturn("n");
        expect(inMock.readLine()).andReturn("Description");

        serviceMock.createCertificate(anyString(), anyString(), anyString(), anyBoolean(), anyObject(), anyString());
        expectLastCall().andThrow(new RuntimeException("Error"));

        outMock.error("Error");
        expectLastCall().once();

        expect(inMock.readMenuNumber(6)).andReturn(6);

        replay(outMock, inMock, serviceMock);
        sut.show();
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testNavigateToChangeMenu() {
        expect(inMock.readMenuNumber(6)).andReturn(4);
        expect(inMock.readInt("Invalid ID.")).andThrow(new IllegalArgumentException("Invalid ID."));

        outMock.error("Invalid ID.");
        expectLastCall().once();

        expect(inMock.readMenuNumber(6)).andReturn(6);

        replay(outMock, inMock, serviceMock);
        sut.show();
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testNavigateToDeleteMenu() {
        expect(inMock.readMenuNumber(6)).andReturn(5);

        expect(inMock.readInt("Invalid ID.")).andThrow(new IllegalArgumentException("Invalid ID."));

        outMock.error("Invalid ID.");
        expectLastCall().once();

        expect(inMock.readMenuNumber(6)).andReturn(6);

        replay(outMock, inMock, serviceMock);
        sut.show();
        verify(outMock, inMock, serviceMock);
    }
}
