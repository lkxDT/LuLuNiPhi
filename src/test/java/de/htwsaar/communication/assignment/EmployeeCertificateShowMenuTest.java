package de.htwsaar.communication.assignment;

import de.htwsaar.application.CertificateService;
import de.htwsaar.application.EmployeeService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;
import de.htwsaar.domain.Employee;
import de.htwsaar.MuteConsoleExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Collections;
import java.util.Optional;

import static org.easymock.EasyMock.*;

@ExtendWith(MuteConsoleExtension.class)
class EmployeeCertificateShowMenuTest {

    private OutputRenderer outMock;
    private InputReader inMock;
    private CertificateService certServiceMock;
    private EmployeeService empServiceMock;

    @BeforeEach
    void setUp() {
        outMock = createNiceMock(OutputRenderer.class);
        inMock = createMock(InputReader.class);
        certServiceMock = createMock(CertificateService.class);
        empServiceMock = createMock(EmployeeService.class);
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
    void testShowCertificatesSuccess() {
        int empId = 1;
        Employee emp = createTestEmployee(empId);

        expect(inMock.readLine()).andReturn(String.valueOf(empId));
        expect(empServiceMock.findEmployeeByID(empId)).andReturn(Optional.of(emp));

        outMock.info("Certificates of John Doe:");
        expectLastCall().once();

        expect(certServiceMock.findCertificatesOfEmployee(empId)).andReturn(Collections.emptyList());

        outMock.employeeCertificateTable(Collections.emptyList());
        expectLastCall().once();

        replay(outMock, inMock, certServiceMock, empServiceMock);
        EmployeeCertificateShowMenu.show(outMock, inMock, empServiceMock, certServiceMock);
        verify(outMock, inMock, certServiceMock, empServiceMock);
    }

    @Test
    void testEmployeeNotFound() {
        int empId = 99;

        expect(inMock.readLine()).andReturn(String.valueOf(empId));
        expect(empServiceMock.findEmployeeByID(empId)).andReturn(Optional.empty());

        outMock.error("Employee with ID " + empId + " not found.");
        expectLastCall().once();

        replay(outMock, inMock, certServiceMock, empServiceMock);
        EmployeeCertificateShowMenu.show(outMock, inMock, empServiceMock, certServiceMock);
        verify(outMock, inMock, certServiceMock, empServiceMock);
    }

    @Test
    void testInvalidEmployeeIdFormat() {
        expect(inMock.readLine()).andReturn("abc");

        outMock.error("Invalid employee ID.");
        expectLastCall().once();

        replay(outMock, inMock, certServiceMock, empServiceMock);
        EmployeeCertificateShowMenu.show(outMock, inMock, empServiceMock, certServiceMock);
        verify(outMock, inMock, certServiceMock, empServiceMock);
    }
}
