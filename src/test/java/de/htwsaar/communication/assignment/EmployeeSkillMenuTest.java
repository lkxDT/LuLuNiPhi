package de.htwsaar.communication.assignment;

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
class EmployeeSkillMenuTest {

    private OutputRenderer outMock;
    private InputReader inMock;
    private SkillService skillServiceMock;
    private EmployeeService empServiceMock;
    private EmployeeSkillMenu sut;

    @BeforeEach
    void setUp() {
        outMock = createNiceMock(OutputRenderer.class);
        inMock = createMock(InputReader.class);
        skillServiceMock = createMock(SkillService.class);
        empServiceMock = createMock(EmployeeService.class);
        sut = new EmployeeSkillMenu(empServiceMock, skillServiceMock, inMock, outMock);
    }

    @Test
    void testNavigateToAssignMenu() {
        expect(inMock.readMenuNumber(5)).andReturn(1);
        expect(inMock.readInt("Invalid employee ID.")).andThrow(new IllegalArgumentException("Invalid employee ID."));

        outMock.error("Invalid employee ID.");
        expectLastCall().once();

        expect(inMock.readMenuNumber(5)).andReturn(5);

        replay(outMock, inMock, skillServiceMock, empServiceMock);
        sut.show();
        verify(outMock, inMock, skillServiceMock, empServiceMock);
    }

    @Test
    void testNavigateToShowMenu() {
        expect(inMock.readMenuNumber(5)).andReturn(2);
        expect(inMock.readInt("Invalid ID.")).andThrow(new IllegalArgumentException("Invalid ID."));

        outMock.error("Invalid ID.");
        expectLastCall().once();

        expect(inMock.readMenuNumber(5)).andReturn(5);

        replay(outMock, inMock, skillServiceMock, empServiceMock);
        sut.show();
        verify(outMock, inMock, skillServiceMock, empServiceMock);
    }

    @Test
    void testNavigateToUpdateMenu() {
        expect(inMock.readMenuNumber(5)).andReturn(3);
        expect(inMock.readInt("Invalid ID.")).andThrow(new IllegalArgumentException("Invalid ID."));

        outMock.error("Invalid ID.");
        expectLastCall().once();

        expect(inMock.readMenuNumber(5)).andReturn(5);

        replay(outMock, inMock, skillServiceMock, empServiceMock);
        sut.show();
        verify(outMock, inMock, skillServiceMock, empServiceMock);
    }

    @Test
    void testNavigateToRemoveMenu() {
        expect(inMock.readMenuNumber(5)).andReturn(4);
        expect(inMock.readInt("Invalid ID.")).andThrow(new IllegalArgumentException("Invalid ID."));

        outMock.error("Invalid ID.");
        expectLastCall().once();

        expect(inMock.readMenuNumber(5)).andReturn(5);

        replay(outMock, inMock, skillServiceMock, empServiceMock);
        sut.show();
        verify(outMock, inMock, skillServiceMock, empServiceMock);
    }

    @Test
    void testReturnImmediately() {
        expect(inMock.readMenuNumber(5)).andReturn(5);

        replay(outMock, inMock, skillServiceMock, empServiceMock);
        sut.show();
        verify(outMock, inMock, skillServiceMock, empServiceMock);
    }
}