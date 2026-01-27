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
class CertificateAssignmentUpdateMenuTest {

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
    void testUpdateSuccessWithNewValues() {
        int empId = 1;
        int certId = 10;
        Employee emp = createTestEmployee(empId);
        AssignedCertificate existing = createAssignedCert(certId);
        List<AssignedCertificate> certList = List.of(existing);

        expect(inMock.readInt("Invalid employee ID.")).andReturn(empId);
        expect(empServiceMock.findEmployeeByID(empId)).andReturn(Optional.of(emp));
        expect(certServiceMock.findCertificatesOfEmployee(empId)).andReturn(certList);

        outMock.employeeCertificateTable(certList);
        expectLastCall().once();

        expect(inMock.readInt("Invalid certificate ID.")).andReturn(certId);
        expect(inMock.readLine()).andReturn("2026-02-01");
        expect(inMock.readLine()).andReturn("CERT-999");
        expect(inMock.readLine()).andReturn("EXPIRED");

        certServiceMock.updateCertificateAssignment(empId, certId, "2026-02-01", "CERT-999", "EXPIRED");
        expectLastCall().once();

        outMock.info("Certificate assignment updated.");
        expectLastCall().once();

        replay(outMock, inMock, certServiceMock, empServiceMock);
        CertificateAssignmentUpdateMenu.show(outMock, inMock, empServiceMock, certServiceMock);
        verify(outMock, inMock, certServiceMock, empServiceMock);
    }

    @Test
    void testUpdateKeepingExistingValues() {
        int empId = 1;
        int certId = 10;
        AssignedCertificate existing = createAssignedCert(certId);

        expect(inMock.readInt(anyString())).andReturn(empId);
        expect(empServiceMock.findEmployeeByID(empId)).andReturn(Optional.of(createTestEmployee(empId)));
        expect(certServiceMock.findCertificatesOfEmployee(empId)).andReturn(List.of(existing));
        expect(inMock.readInt(anyString())).andReturn(certId);
        expect(inMock.readLine()).andReturn("   ");
        expect(inMock.readLine()).andReturn("");
        expect(inMock.readLine()).andReturn(" ");

        certServiceMock.updateCertificateAssignment(empId, certId, "2026-01-01", "CERT-123", "ACTIVE");
        expectLastCall().once();

        outMock.info("Certificate assignment updated.");
        expectLastCall().once();

        replay(outMock, inMock, certServiceMock, empServiceMock);
        CertificateAssignmentUpdateMenu.show(outMock, inMock, empServiceMock, certServiceMock);
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
        CertificateAssignmentUpdateMenu.show(outMock, inMock, empServiceMock, certServiceMock);
        verify(outMock, inMock, certServiceMock, empServiceMock);
    }

    @Test
    void testCertificateNotAssignedToThisEmployee() {
        int empId = 1;
        AssignedCertificate cert = createAssignedCert(55);

        expect(inMock.readInt(anyString())).andReturn(empId);
        expect(empServiceMock.findEmployeeByID(empId)).andReturn(Optional.of(createTestEmployee(empId)));
        expect(certServiceMock.findCertificatesOfEmployee(empId)).andReturn(List.of(cert));
        expect(inMock.readInt(anyString())).andReturn(10);

        outMock.error("Certificate not assigned.");
        expectLastCall().once();

        replay(outMock, inMock, certServiceMock, empServiceMock);
        CertificateAssignmentUpdateMenu.show(outMock, inMock, empServiceMock, certServiceMock);
        verify(outMock, inMock, certServiceMock, empServiceMock);
    }

    @Test
    void testInvalidEmployeeId() {
        expect(inMock.readInt("Invalid employee ID."))
                .andThrow(new IllegalArgumentException("Invalid employee ID."));

        outMock.error("Invalid employee ID.");
        expectLastCall().once();

        replay(outMock, inMock, certServiceMock, empServiceMock);
        CertificateAssignmentUpdateMenu.show(outMock, inMock, empServiceMock, certServiceMock);
        verify(outMock, inMock, certServiceMock, empServiceMock);
    }

    @Test
    void testServiceErrorOnUpdate() {
        int empId = 1;
        int certId = 10;
        AssignedCertificate cert = createAssignedCert(certId);

        expect(inMock.readInt(anyString())).andReturn(empId);
        expect(empServiceMock.findEmployeeByID(empId)).andReturn(Optional.of(createTestEmployee(empId)));
        expect(certServiceMock.findCertificatesOfEmployee(empId)).andReturn(List.of(cert));
        expect(inMock.readInt(anyString())).andReturn(certId);
        expect(inMock.readLine()).andStubReturn("");

        certServiceMock.updateCertificateAssignment(anyInt(), anyInt(), anyString(), anyString(), anyString());
        expectLastCall().andThrow(new RuntimeException("Update failed"));

        outMock.error("Update failed");
        expectLastCall().once();

        replay(outMock, inMock, certServiceMock, empServiceMock);
        CertificateAssignmentUpdateMenu.show(outMock, inMock, empServiceMock, certServiceMock);
        verify(outMock, inMock, certServiceMock, empServiceMock);
    }
}