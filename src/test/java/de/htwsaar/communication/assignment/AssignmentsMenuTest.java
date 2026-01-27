package de.htwsaar.communication.assignment;

import de.htwsaar.application.CertificateService;
import de.htwsaar.application.EmployeeService;
import de.htwsaar.application.SkillService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;
import de.htwsaar.MuteConsoleExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.easymock.EasyMock.*;

@ExtendWith(MuteConsoleExtension.class)
class AssignmentsMenuTest {

    private OutputRenderer outMock;
    private InputReader inMock;
    private EmployeeService empServiceMock;
    private SkillService skillServiceMock;
    private CertificateService certServiceMock;
    private AssignmentsMenu sut;

    @BeforeEach
    void setUp() {
        outMock = createNiceMock(OutputRenderer.class);
        inMock = createMock(InputReader.class);
        empServiceMock = createMock(EmployeeService.class);
        skillServiceMock = createMock(SkillService.class);
        certServiceMock = createMock(CertificateService.class);
        sut = new AssignmentsMenu(empServiceMock, skillServiceMock, certServiceMock, inMock, outMock);
    }

    @Test
    void testReturnImmediately() {
        expect(inMock.readMenuNumber(9)).andReturn(9);

        replay(outMock, inMock, empServiceMock, skillServiceMock, certServiceMock);
        sut.show();
        verify(outMock, inMock, empServiceMock, skillServiceMock, certServiceMock);
    }

    @Test
    void testNavigateToShowEmployeeCertificates() {
        expect(inMock.readMenuNumber(9)).andReturn(1);
        expect(inMock.readLine()).andReturn("invalid");

        outMock.error("Invalid employee ID.");
        expectLastCall().once();

        expect(inMock.readMenuNumber(9)).andReturn(9);

        replay(outMock, inMock, empServiceMock, skillServiceMock, certServiceMock);
        sut.show();
        verify(outMock, inMock, empServiceMock, skillServiceMock, certServiceMock);
    }

    @Test
    void testNavigateToShowEmployeeSkills() {
        expect(inMock.readMenuNumber(9)).andReturn(2);
        expect(inMock.readInt("Invalid ID."))
                .andThrow(new IllegalArgumentException("Invalid ID."));

        outMock.error("Invalid ID.");
        expectLastCall().once();

        expect(inMock.readMenuNumber(9)).andReturn(9);

        replay(outMock, inMock, empServiceMock, skillServiceMock, certServiceMock);
        sut.show();
        verify(outMock, inMock, empServiceMock, skillServiceMock, certServiceMock);
    }

    @Test
    void testNavigateToAssignCertificate() {
        expect(inMock.readMenuNumber(9)).andReturn(3);
        expect(inMock.readLine()).andReturn("invalid");

        outMock.error("Invalid employee ID.");
        expectLastCall().once();

        expect(inMock.readMenuNumber(9)).andReturn(9);

        replay(outMock, inMock, empServiceMock, skillServiceMock, certServiceMock);
        sut.show();
        verify(outMock, inMock, empServiceMock, skillServiceMock, certServiceMock);
    }

    @Test
    void testNavigateToAssignSkill() {
        expect(inMock.readMenuNumber(9)).andReturn(4);
        expect(inMock.readInt("Invalid employee ID."))
                .andThrow(new IllegalArgumentException("Invalid employee ID."));

        outMock.error("Invalid employee ID.");
        expectLastCall().once();

        expect(inMock.readMenuNumber(9)).andReturn(9);

        replay(outMock, inMock, empServiceMock, skillServiceMock, certServiceMock);
        sut.show();
        verify(outMock, inMock, empServiceMock, skillServiceMock, certServiceMock);
    }

    @Test
    void testNavigateToUpdateCertificateAssignment() {
        expect(inMock.readMenuNumber(9)).andReturn(5);
        expect(inMock.readInt("Invalid employee ID."))
                .andThrow(new IllegalArgumentException("Invalid employee ID."));

        outMock.error("Invalid employee ID.");
        expectLastCall().once();

        expect(inMock.readMenuNumber(9)).andReturn(9);

        replay(outMock, inMock, empServiceMock, skillServiceMock, certServiceMock);
        sut.show();
        verify(outMock, inMock, empServiceMock, skillServiceMock, certServiceMock);
    }

    @Test
    void testNavigateToUpdateSkillAssignment() {
        expect(inMock.readMenuNumber(9)).andReturn(6);
        expect(inMock.readInt("Invalid ID."))
                .andThrow(new IllegalArgumentException("Invalid ID."));

        outMock.error("Invalid ID.");
        expectLastCall().once();

        expect(inMock.readMenuNumber(9)).andReturn(9);

        replay(outMock, inMock, empServiceMock, skillServiceMock, certServiceMock);
        sut.show();
        verify(outMock, inMock, empServiceMock, skillServiceMock, certServiceMock);
    }

    @Test
    void testNavigateToRemoveCertificateAssignment() {
        expect(inMock.readMenuNumber(9)).andReturn(7);
        expect(inMock.readInt("Invalid employee ID."))
                .andThrow(new IllegalArgumentException("Invalid employee ID."));

        outMock.error("Invalid employee ID.");
        expectLastCall().once();

        expect(inMock.readMenuNumber(9)).andReturn(9);

        replay(outMock, inMock, empServiceMock, skillServiceMock, certServiceMock);
        sut.show();
        verify(outMock, inMock, empServiceMock, skillServiceMock, certServiceMock);
    }

    @Test
    void testNavigateToRemoveSkillAssignment() {
        expect(inMock.readMenuNumber(9)).andReturn(8);
        expect(inMock.readInt("Invalid ID."))
                .andThrow(new IllegalArgumentException("Invalid ID."));

        outMock.error("Invalid ID.");
        expectLastCall().once();

        expect(inMock.readMenuNumber(9)).andReturn(9);

        replay(outMock, inMock, empServiceMock, skillServiceMock, certServiceMock);
        sut.show();
        verify(outMock, inMock, empServiceMock, skillServiceMock, certServiceMock);
    }
}