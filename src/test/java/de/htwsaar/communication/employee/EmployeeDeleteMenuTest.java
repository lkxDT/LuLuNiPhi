package de.htwsaar.communication.employee;

import de.htwsaar.application.EmployeeService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;
import de.htwsaar.domain.Employee;
import de.htwsaar.MuteConsoleExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.Optional;

import static org.easymock.EasyMock.*;

@ExtendWith(MuteConsoleExtension.class)
class EmployeeDeleteMenuTest {

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
                id, "John", "Doe", "john@company.com",
                "Dev", "Active", "2026-01-01", 40
        );
    }

    @Test
    void testDeleteSuccess() {
        int id = 1;
        Employee emp = createTestEmployee(id);

        expect(inMock.readLine()).andReturn(String.valueOf(id));
        expect(serviceMock.findEmployeeByID(id)).andReturn(Optional.of(emp));
        expect(inMock.readLine()).andReturn("DELETE");

        serviceMock.deleteEmployeeById(id);
        expectLastCall().once();

        outMock.info("Employee deleted successfully.");
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        EmployeeDeleteMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testDeleteCancelled() {
        int id = 1;
        Employee emp = createTestEmployee(id);

        expect(inMock.readLine()).andReturn(String.valueOf(id));
        expect(serviceMock.findEmployeeByID(id)).andReturn(Optional.of(emp));
        expect(inMock.readLine()).andReturn("no");

        outMock.info("Deletion cancelled.");
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        EmployeeDeleteMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testInvalidIDFormat() {
        expect(inMock.readLine()).andReturn("abc");

        outMock.error("Invalid employee ID.");
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        EmployeeDeleteMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testEmployeeNotFound() {
        int id = 99;
        expect(inMock.readLine()).andReturn(String.valueOf(id));
        expect(serviceMock.findEmployeeByID(id)).andReturn(Optional.empty());

        outMock.error("Employee with ID " + id + " not found.");
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        EmployeeDeleteMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testDeleteServiceError() {
        int id = 1;
        Employee emp = createTestEmployee(id);

        expect(inMock.readLine()).andReturn(String.valueOf(id));
        expect(serviceMock.findEmployeeByID(id)).andReturn(Optional.of(emp));
        expect(inMock.readLine()).andReturn("DELETE");

        serviceMock.deleteEmployeeById(id);
        expectLastCall().andThrow(new RuntimeException("Database error"));

        outMock.error("Database error");
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        EmployeeDeleteMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }
}
