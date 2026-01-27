package de.htwsaar.communication.employee;

import de.htwsaar.application.EmployeeService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;
import de.htwsaar.MuteConsoleExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Collections;

import static org.easymock.EasyMock.*;

@ExtendWith(MuteConsoleExtension.class)
class EmployeeMenuTest {

    private OutputRenderer outMock;
    private InputReader inMock;
    private EmployeeService serviceMock;
    private EmployeeMenu sut;

    @BeforeEach
    void setUp() {
        outMock = createNiceMock(OutputRenderer.class);
        inMock = createMock(InputReader.class);
        serviceMock = createMock(EmployeeService.class);
        sut = new EmployeeMenu(serviceMock, inMock, outMock);
    }

    @Test
    void testShowAll() {
        expect(inMock.readMenuNumber(6)).andReturn(1);
        expect(serviceMock.findAllEmployees()).andReturn(Collections.emptyList());

        outMock.employeeTable(Collections.emptyList());
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
    void testNavigateToSearchMenu() {
        expect(inMock.readMenuNumber(6)).andReturn(2);
        expect(inMock.readMenuNumber(3)).andReturn(3);
        expect(inMock.readMenuNumber(6)).andReturn(6);

        replay(outMock, inMock, serviceMock);
        sut.show();
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testNavigateToAddMenu() {
        expect(inMock.readMenuNumber(6)).andReturn(3);
        expect(inMock.readLine()).andReturn("John");
        expect(inMock.readLine()).andReturn("Doe");
        expect(inMock.readLine()).andReturn("Dev");
        expect(inMock.readLine()).andReturn("Active");
        expect(inMock.readLine()).andReturn("2026-01-01");
        expect(inMock.readInt("Invalid working hours."))
                .andThrow(new IllegalArgumentException("Invalid working hours."));

        outMock.error("Invalid working hours.");
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
        expect(inMock.readLine()).andReturn("");

        outMock.error("Invalid employee ID.");
        expectLastCall().once();
        expect(inMock.readMenuNumber(6)).andReturn(6);

        replay(outMock, inMock, serviceMock);
        sut.show();
        verify(outMock, inMock, serviceMock);
    }
}
