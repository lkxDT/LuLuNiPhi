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
class EmployeeChangeMenuTest {

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
    void testChangeAllFieldsWithEmailRegeneration() {
        int id = 1;
        Employee oldEmp = createTestEmployee(id);
        String newMail = "j.smith@company.com";

        expect(inMock.readInt("Invalid ID.")).andReturn(id);
        expect(serviceMock.findEmployeeByID(id)).andReturn(Optional.of(oldEmp));

        expect(inMock.readLine()).andReturn("Jane");
        expect(inMock.readLine()).andReturn("Smith");
        expect(inMock.readLine()).andReturn("Lead");
        expect(inMock.readLine()).andReturn("Sick");
        expect(inMock.readLine()).andReturn("2026-02-01");
        expect(inMock.readLine()).andReturn("35");

        expect(inMock.readLine()).andReturn("y");
        expect(serviceMock.generateEmailForUpdate("Jane", "Smith", id)).andReturn(newMail);

        serviceMock.updateEmployee(anyObject(Employee.class));
        expectLastCall().once();

        outMock.info("Employee updated.");
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        EmployeeChangeMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testChangeNameWithoutEmailRegeneration() {
        int id = 1;
        Employee oldEmp = createTestEmployee(id);

        expect(inMock.readInt("Invalid ID.")).andReturn(id);
        expect(serviceMock.findEmployeeByID(id)).andReturn(Optional.of(oldEmp));
        expect(inMock.readLine()).andReturn("Jane");
        expect(inMock.readLine()).andReturn("Smith");
        expect(inMock.readLine()).andReturn("").times(4);
        expect(inMock.readLine()).andReturn("n");

        serviceMock.updateEmployee(anyObject(Employee.class));
        expectLastCall().once();

        outMock.info("Employee updated.");
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        EmployeeChangeMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testKeepCurrentValuesWithEmptyInput() {
        int id = 1;
        Employee emp = createTestEmployee(id);

        expect(inMock.readInt("Invalid ID.")).andReturn(id);
        expect(serviceMock.findEmployeeByID(id)).andReturn(Optional.of(emp));
        expect(inMock.readLine()).andReturn("").times(6);

        serviceMock.updateEmployee(anyObject(Employee.class));
        expectLastCall().once();

        outMock.info("Employee updated.");
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        EmployeeChangeMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testInvalidID() {
        expect(inMock.readInt("Invalid ID.")).andThrow(new IllegalArgumentException("Invalid ID."));

        outMock.error("Invalid ID.");
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        EmployeeChangeMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testEmployeeNotFound() {
        expect(inMock.readInt("Invalid ID.")).andReturn(99);
        expect(serviceMock.findEmployeeByID(99)).andReturn(Optional.empty());

        outMock.error("Employee not found.");
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        EmployeeChangeMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testInvalidWorkingHoursInput() {
        int id = 1;
        Employee emp = createTestEmployee(id);

        expect(inMock.readInt("Invalid ID.")).andReturn(id);
        expect(serviceMock.findEmployeeByID(id)).andReturn(Optional.of(emp));
        expect(inMock.readLine()).andReturn("").times(5);
        expect(inMock.readLine()).andReturn("not-a-number");

        outMock.error("Invalid working hours.");
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        EmployeeChangeMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testUpdateServiceError() {
        int id = 1;
        Employee emp = createTestEmployee(id);

        expect(inMock.readInt("Invalid ID.")).andReturn(id);
        expect(serviceMock.findEmployeeByID(id)).andReturn(Optional.of(emp));
        expect(inMock.readLine()).andReturn("").times(6);

        serviceMock.updateEmployee(anyObject(Employee.class));
        expectLastCall().andThrow(new RuntimeException("Update failed"));

        outMock.error("Update failed");
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        EmployeeChangeMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }
}
