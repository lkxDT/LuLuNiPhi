package de.htwsaar.communication.assignment;

import de.htwsaar.application.CertificateService;
import de.htwsaar.application.EmployeeService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;
import de.htwsaar.domain.AssignedCertificate;
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
class CertificateAssignmentRemoveMenuTest {

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
     * Creates a dummy assigned certificate for tests.
     * @param certId Certificate ID
     * @return AssignedCertificate object
     */
    private AssignedCertificate createAssignedCert(int certId) {
        return new AssignedCertificate(
                certId, "Java Expert", "IT", "External",
                "2026-01-01", "2028-01-01", "CERT-123", "ACTIVE"
        );
    }

    @Test
    void testRemoveSuccess() {
        int empId = 1;
        int certId = 10;
        AssignedCertificate cert = createAssignedCert(certId);

        expect(inMock.readInt(anyString())).andReturn(empId);
        expect(empServiceMock.findEmployeeByID(empId)).andReturn(Optional.of(createTestEmployee(empId)));
        expect(certServiceMock.findCertificatesOfEmployee(empId)).andReturn(List.of(cert));

        outMock.employeeCertificateTable(anyObject());
        expectLastCall().once();

        expect(inMock.readInt(anyString())).andReturn(certId);
        expect(inMock.readLine()).andReturn("y");

        certServiceMock.removeCertificateFromEmployee(empId, certId);
        expectLastCall().once();

        outMock.info("Certificate assignment removed.");
        expectLastCall().once();

        replay(outMock, inMock, certServiceMock, empServiceMock);
        CertificateAssignmentRemoveMenu.show(outMock, inMock, empServiceMock, certServiceMock);
        verify(outMock, inMock, certServiceMock, empServiceMock);
    }

    @Test
    void testRemoveCanceled() {
        int empId = 1;
        int certId = 10;
        AssignedCertificate cert = createAssignedCert(certId);

        expect(inMock.readInt(anyString())).andReturn(empId);
        expect(empServiceMock.findEmployeeByID(empId)).andReturn(Optional.of(createTestEmployee(empId)));
        expect(certServiceMock.findCertificatesOfEmployee(empId)).andReturn(List.of(cert));
        expect(inMock.readInt(anyString())).andReturn(certId);
        expect(inMock.readLine()).andReturn("n");

        outMock.info("Removal canceled.");
        expectLastCall().once();

        replay(outMock, inMock, certServiceMock, empServiceMock);
        CertificateAssignmentRemoveMenu.show(outMock, inMock, empServiceMock, certServiceMock);
        verify(outMock, inMock, certServiceMock, empServiceMock);
    }

    @Test
    void testCertificateNotAssigned() {
        int empId = 1;
        AssignedCertificate cert = createAssignedCert(55);

        expect(inMock.readInt(anyString())).andReturn(empId);
        expect(empServiceMock.findEmployeeByID(empId)).andReturn(Optional.of(createTestEmployee(empId)));
        expect(certServiceMock.findCertificatesOfEmployee(empId)).andReturn(List.of(cert));
        expect(inMock.readInt(anyString())).andReturn(10);

        outMock.error("Certificate not assigned.");
        expectLastCall().once();

        replay(outMock, inMock, certServiceMock, empServiceMock);
        CertificateAssignmentRemoveMenu.show(outMock, inMock, empServiceMock, certServiceMock);
        verify(outMock, inMock, certServiceMock, empServiceMock);
    }

    @Test
    void testEmployeeNotFound() {
        expect(inMock.readInt(anyString())).andReturn(99);
        expect(empServiceMock.findEmployeeByID(99)).andReturn(Optional.empty());

        outMock.error("Employee not found.");
        expectLastCall().once();

        replay(outMock, inMock, certServiceMock, empServiceMock);
        CertificateAssignmentRemoveMenu.show(outMock, inMock, empServiceMock, certServiceMock);
        verify(outMock, inMock, certServiceMock, empServiceMock);
    }

    @Test
    void testNoCertificatesAssigned() {
        int empId = 1;

        expect(inMock.readInt(anyString())).andReturn(empId);
        expect(empServiceMock.findEmployeeByID(empId)).andReturn(Optional.of(createTestEmployee(empId)));
        expect(certServiceMock.findCertificatesOfEmployee(empId)).andReturn(Collections.emptyList());

        outMock.info("No certificates assigned.");
        expectLastCall().once();

        replay(outMock, inMock, certServiceMock, empServiceMock);
        CertificateAssignmentRemoveMenu.show(outMock, inMock, empServiceMock, certServiceMock);
        verify(outMock, inMock, certServiceMock, empServiceMock);
    }

    @Test
    void testServiceErrorOnRemove() {
        int empId = 1;
        int certId = 10;
        AssignedCertificate cert = createAssignedCert(certId);

        expect(inMock.readInt(anyString())).andReturn(empId);
        expect(empServiceMock.findEmployeeByID(empId)).andReturn(Optional.of(createTestEmployee(empId)));
        expect(certServiceMock.findCertificatesOfEmployee(empId)).andReturn(List.of(cert));
        expect(inMock.readInt(anyString())).andReturn(certId);
        expect(inMock.readLine()).andReturn("y");

        certServiceMock.removeCertificateFromEmployee(empId, certId);
        expectLastCall().andThrow(new RuntimeException("Deletion failed"));

        outMock.error("Deletion failed");
        expectLastCall().once();

        replay(outMock, inMock, certServiceMock, empServiceMock);
        CertificateAssignmentRemoveMenu.show(outMock, inMock, empServiceMock, certServiceMock);
        verify(outMock, inMock, certServiceMock, empServiceMock);
    }
}
