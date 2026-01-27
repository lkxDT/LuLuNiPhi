package de.htwsaar.communication.employee;

import de.htwsaar.application.EmployeeService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;
import de.htwsaar.domain.Employee;
import de.htwsaar.MuteConsoleExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.easymock.EasyMock.*;

@ExtendWith(MuteConsoleExtension.class)
class EmployeeSearchMenuTest {

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
     * @param id         EmployeeID
     * @param firstName  Firstname of the Employee
     * @param lastName   Lastname of the Employee
     * @return           a Employee object
     */
    private Employee createTestEmployee(int id, String firstName, String lastName) {
        return new Employee(
                id, firstName, lastName,
                firstName.toLowerCase() + "@company.com",
                "Developer", "Active", "2026-01-01", 40
        );
    }

    @Test
    void testSearchByIDSuccess() {
        Employee emp = createTestEmployee(1, "John", "Doe");

        expect(inMock.readMenuNumber(3)).andReturn(1);
        expect(inMock.readInt(anyString())).andReturn(1);
        expect(serviceMock.findEmployeeByID(1)).andReturn(Optional.of(emp));

        outMock.employeeTable(List.of(emp));
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        EmployeeSearchMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testSearchByIDNotFound() {
        expect(inMock.readMenuNumber(3)).andReturn(1);
        expect(inMock.readInt(anyString())).andReturn(99);
        expect(serviceMock.findEmployeeByID(99)).andReturn(Optional.empty());

        outMock.error("Employee not found.");
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        EmployeeSearchMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testSearchByLastName() {
        String lastName = "Smith";
        List<Employee> results = List.of(createTestEmployee(2, "Alice", "Smith"));

        expect(inMock.readMenuNumber(3)).andReturn(2);
        expect(inMock.readLine()).andReturn(lastName);
        expect(serviceMock.findEmployeesByLastName(lastName)).andReturn(results);

        outMock.employeeTable(results);
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        EmployeeSearchMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testSearchByIDInvalidInput() {
        expect(inMock.readMenuNumber(3)).andReturn(1);
        expect(inMock.readInt(anyString())).andThrow(new IllegalArgumentException("Invalid ID."));

        outMock.error("Invalid ID.");
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        EmployeeSearchMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testReturnOption() {
        expect(inMock.readMenuNumber(3)).andReturn(3);

        replay(outMock, inMock, serviceMock);
        EmployeeSearchMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testSearchByLastNameNotFound() {
        String lastName = "Unknown";

        expect(inMock.readMenuNumber(3)).andReturn(2);
        expect(inMock.readLine()).andReturn(lastName);
        expect(serviceMock.findEmployeesByLastName(lastName)).andReturn(Collections.emptyList());

        outMock.employeeTable(Collections.emptyList());
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        EmployeeSearchMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }

    @Test
    void testSearchByLastNameBlankInput() {
        expect(inMock.readMenuNumber(3)).andReturn(2);
        expect(inMock.readLine()).andReturn("   ");
        expect(serviceMock.findEmployeesByLastName("   ")).andReturn(Collections.emptyList());

        outMock.employeeTable(Collections.emptyList());
        expectLastCall().once();

        replay(outMock, inMock, serviceMock);
        EmployeeSearchMenu.show(outMock, inMock, serviceMock);
        verify(outMock, inMock, serviceMock);
    }
}