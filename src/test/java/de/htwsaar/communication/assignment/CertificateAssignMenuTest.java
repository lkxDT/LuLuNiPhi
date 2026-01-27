package de.htwsaar.communication.assignment;

import de.htwsaar.application.CertificateService;
import de.htwsaar.application.EmployeeService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;
import de.htwsaar.domain.Certificate;
import de.htwsaar.domain.Employee;
import de.htwsaar.MuteConsoleExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Collections;
import java.util.Optional;

import static org.easymock.EasyMock.*;

@ExtendWith(MuteConsoleExtension.class)
class CertificateAssignMenuTest {

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

    /**
     * Creates a dummy Certificate for Tests
     * @param id Certificate ID
     * @return   a Certificate object
     */
    private Certificate createTestCertificate(int id) {
        return new Certificate(
                id, "Java Expert", "IT", "Oracle",
                false, null, "Expert level certification"
        );
    }

    @Test
    void testAssignCertificateSuccess() {
        int empId = 1;
        int certId = 50;
        String date = "2026-01-01";
        Employee emp = createTestEmployee(empId);
        Certificate cert = createTestCertificate(certId);

        expect(inMock.readLine()).andReturn(String.valueOf(empId));
        expect(empServiceMock.findEmployeeByID(empId)).andReturn(Optional.of(emp));

        outMock.info("Available certificates:");
        expectLastCall().once();

        expect(certServiceMock.findAllCertificates()).andReturn(Collections.emptyList());

        outMock.certificateTable(Collections.emptyList());
        expectLastCall().once();

        expect(inMock.readLine()).andReturn(String.valueOf(certId));
        expect(certServiceMock.findCertificateById(certId)).andReturn(Optional.of(cert));
        expect(inMock.readLine()).andReturn(date);

        certServiceMock.assignCertificateToEmployee(empId, certId, date);
        expectLastCall().once();

        outMock.info("Certificate assigned.");
        expectLastCall().once();

        replay(outMock, inMock, certServiceMock, empServiceMock);
        CertificateAssignMenu.show(outMock, inMock, empServiceMock, certServiceMock);
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
        CertificateAssignMenu.show(outMock, inMock, empServiceMock, certServiceMock);
        verify(outMock, inMock, certServiceMock, empServiceMock);
    }

    @Test
    void testInvalidEmployeeIdFormat() {
        expect(inMock.readLine()).andReturn("invalid");

        outMock.error("Invalid employee ID.");
        expectLastCall().once();

        replay(outMock, inMock, certServiceMock, empServiceMock);
        CertificateAssignMenu.show(outMock, inMock, empServiceMock, certServiceMock);
        verify(outMock, inMock, certServiceMock, empServiceMock);
    }

    @Test
    void testCertificateNotFound() {
        int empId = 1;
        int certId = 999;
        Employee emp = createTestEmployee(empId);

        expect(inMock.readLine()).andReturn(String.valueOf(empId));
        expect(empServiceMock.findEmployeeByID(empId)).andReturn(Optional.of(emp));
        expect(certServiceMock.findAllCertificates()).andStubReturn(Collections.emptyList());
        expect(inMock.readLine()).andReturn(String.valueOf(certId));
        expect(certServiceMock.findCertificateById(certId)).andReturn(Optional.empty());

        outMock.error("Certificate with ID " + certId + " not found.");
        expectLastCall().once();

        replay(outMock, inMock, certServiceMock, empServiceMock);
        CertificateAssignMenu.show(outMock, inMock, empServiceMock, certServiceMock);
        verify(outMock, inMock, certServiceMock, empServiceMock);
    }

    @Test
    void testServiceErrorOnAssignment() {
        int empId = 1;
        int certId = 50;
        String date = "2026-01-01";
        Employee emp = createTestEmployee(empId);
        Certificate cert = createTestCertificate(certId);

        expect(inMock.readLine()).andReturn(String.valueOf(empId));
        expect(empServiceMock.findEmployeeByID(empId)).andReturn(Optional.of(emp));
        expect(certServiceMock.findAllCertificates()).andReturn(Collections.emptyList());
        expect(inMock.readLine()).andReturn(String.valueOf(certId));
        expect(certServiceMock.findCertificateById(certId)).andReturn(Optional.of(cert));
        expect(inMock.readLine()).andReturn(date);

        certServiceMock.assignCertificateToEmployee(empId, certId, date);
        expectLastCall().andThrow(new RuntimeException("Error during assignment"));

        outMock.error("Error during assignment");
        expectLastCall().once();

        replay(outMock, inMock, certServiceMock, empServiceMock);
        CertificateAssignMenu.show(outMock, inMock, empServiceMock, certServiceMock);
        verify(outMock, inMock, certServiceMock, empServiceMock);
    }
}