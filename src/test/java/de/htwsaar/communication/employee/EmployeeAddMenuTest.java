package de.htwsaar.communication.employee;

import de.htwsaar.application.EmployeeService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;
import de.htwsaar.domain.Employee;
import de.htwsaar.MuteConsoleExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.easymock.EasyMock.*;

@ExtendWith(MuteConsoleExtension.class)
class EmployeeAddMenuTest {

    private OutputRenderer outMock;
    private InputReader inMock;
    private EmployeeService serviceMock;

    @BeforeEach
    void setUp() {
        outMock = createNiceMock(OutputRenderer.class);
        inMock = createMock(InputReader.class);
        serviceMock = createMock(EmployeeService.class);
    }

    /**
     * Creates a dummy Employee for Tests
     * @param id EmployeeID
     * @return   a Employee object
     */
    private Employee createTestEmployee(int id) {
        return new Employee(
                id, "John", "Doe", "john.doe@company.com",
                "Dev", "Active", "2026-01-01", 40
        );
    }

    @Test
    void testAddEmployeeSuccess() {
        Employee emp = createTestEmployee(1);

        expect(inMock.readLine()).andReturn("John");
        expect(inMock.readLine()).andReturn("Doe");
        expect(inMock.readLine()).andReturn("Dev");
        expect(inMock.readLine()).andReturn("Active");
        expect(inMock.readLine()).andReturn("2026-01-01");
        expect(inMock.readInt("Invalid working hours.")).andReturn(40);
        expect(serviceMock.createEmployee("John", "Doe", "Dev", "Active", "2026-01-01", 40))
                .andReturn(emp);

        outMock.info("Employee created: john.doe@company.com");
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        EmployeeAddMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testAddEmployeeInvalidHours() {
        expect(inMock.readLine()).andReturn("John");
        expect(inMock.readLine()).andReturn("Doe");
        expect(inMock.readLine()).andReturn("Dev");
        expect(inMock.readLine()).andReturn("Active");
        expect(inMock.readLine()).andReturn("2026-01-01");
        expect(inMock.readInt("Invalid working hours."))
                .andThrow(new IllegalArgumentException("Invalid working hours."));

        outMock.error("Invalid working hours.");
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        EmployeeAddMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testAddEmployeeServiceError() {
        expect(inMock.readLine()).andReturn("John");
        expect(inMock.readLine()).andReturn("Doe");
        expect(inMock.readLine()).andReturn("Dev");
        expect(inMock.readLine()).andReturn("Active");
        expect(inMock.readLine()).andReturn("2026-01-01");
        expect(inMock.readInt("Invalid working hours.")).andReturn(40);
        expect(serviceMock.createEmployee(anyString(), anyString(), anyString(), anyString(), anyString(), anyInt()))
                .andThrow(new RuntimeException("Database error"));

        outMock.error("Database error");
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        EmployeeAddMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }
}
