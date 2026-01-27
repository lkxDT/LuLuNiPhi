package de.htwsaar.communication;

import de.htwsaar.communication.assignment.AssignmentsMenu;
import de.htwsaar.communication.certificate.CertificateMenu;
import de.htwsaar.communication.employee.EmployeeMenu;
import de.htwsaar.communication.skill.SkillMenu;
import de.htwsaar.MuteConsoleExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.easymock.EasyMock.*;

@ExtendWith(MuteConsoleExtension.class)
class MenuNavigatorTest {

    private EmployeeMenu employeeMenuMock;
    private CertificateMenu certificateMenuMock;
    private SkillMenu skillMenuMock;
    private AssignmentsMenu assignmentsMenuMock;
    private InputReader inMock;
    private OutputRenderer outMock;
    private MenuNavigator sut;

    @BeforeEach
    void setUp() {
        employeeMenuMock = createMock(EmployeeMenu.class);
        certificateMenuMock = createMock(CertificateMenu.class);
        skillMenuMock = createMock(SkillMenu.class);
        assignmentsMenuMock = createMock(AssignmentsMenu.class);
        inMock = createMock(InputReader.class);
        outMock = createNiceMock(OutputRenderer.class);

        sut = new MenuNavigator(
                employeeMenuMock,
                certificateMenuMock,
                skillMenuMock,
                assignmentsMenuMock,
                inMock,
                outMock
        );
    }

    @Test
    void testNavigateToEmployeeMenu() {
        expect(inMock.readMenuNumber(5)).andReturn(1);

        employeeMenuMock.show();
        expectLastCall().once();

        expect(inMock.readMenuNumber(5)).andReturn(5);

        replay(employeeMenuMock, certificateMenuMock, skillMenuMock, assignmentsMenuMock, inMock, outMock);
        sut.run();
        verify(employeeMenuMock, certificateMenuMock, skillMenuMock, assignmentsMenuMock, inMock, outMock);
    }

    @Test
    void testNavigateToCertificateMenu() {
        expect(inMock.readMenuNumber(5)).andReturn(2);

        certificateMenuMock.show();
        expectLastCall().once();

        expect(inMock.readMenuNumber(5)).andReturn(5);

        replay(employeeMenuMock, certificateMenuMock, skillMenuMock, assignmentsMenuMock, inMock, outMock);
        sut.run();
        verify(employeeMenuMock, certificateMenuMock, skillMenuMock, assignmentsMenuMock, inMock, outMock);
    }

    @Test
    void testNavigateToSkillMenu() {
        expect(inMock.readMenuNumber(5)).andReturn(3);

        skillMenuMock.show();
        expectLastCall().once();

        expect(inMock.readMenuNumber(5)).andReturn(5);

        replay(employeeMenuMock, certificateMenuMock, skillMenuMock, assignmentsMenuMock, inMock, outMock);
        sut.run();
        verify(employeeMenuMock, certificateMenuMock, skillMenuMock, assignmentsMenuMock, inMock, outMock);
    }

    @Test
    void testNavigateToAssignmentsMenu() {
        expect(inMock.readMenuNumber(5)).andReturn(4);

        assignmentsMenuMock.show();
        expectLastCall().once();

        expect(inMock.readMenuNumber(5)).andReturn(5);

        replay(employeeMenuMock, certificateMenuMock, skillMenuMock, assignmentsMenuMock, inMock, outMock);
        sut.run();
        verify(employeeMenuMock, certificateMenuMock, skillMenuMock, assignmentsMenuMock, inMock, outMock);
    }

    @Test
    void testExitImmediately() {
        expect(inMock.readMenuNumber(5)).andReturn(5);

        replay(employeeMenuMock, certificateMenuMock, skillMenuMock, assignmentsMenuMock, inMock, outMock);
        sut.run();
        verify(employeeMenuMock, certificateMenuMock, skillMenuMock, assignmentsMenuMock, inMock, outMock);
    }
}
