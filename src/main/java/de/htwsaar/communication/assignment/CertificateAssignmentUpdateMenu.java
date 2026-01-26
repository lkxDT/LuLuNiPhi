package de.htwsaar.communication.assignment;

import de.htwsaar.application.CertificateService;
import de.htwsaar.application.EmployeeService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;
import de.htwsaar.domain.AssignedCertificate;

import java.util.List;

/**
 * Menu for updating certificate assignments of an employee.
 */
public class CertificateAssignmentUpdateMenu {
    /**
     * Utility class constructor.
     * Prevents instantiation.
     */
    private CertificateAssignmentUpdateMenu() {}

    /**
     * Displays the menu to update an existing certificate assignment.
     *
     * @param out         OutputRenderer for console output
     * @param in          InputReader for user input
     * @param empService  Service for employee operations
     * @param certService Service for certificate operations
     */
    public static void show(OutputRenderer out, InputReader in, EmployeeService empService, CertificateService certService) {
        out.section("Update Certificate Assignment");

        System.out.print("Employee ID: ");
        int empId;
        try {
            empId = in.readInt("Invalid employee ID.");
        } catch (IllegalArgumentException e) {
            out.error(e.getMessage());
            return;
        }

        if (empService.findEmployeeByID(empId).isEmpty()) {
            out.error("Employee not found.");
            return;
        }

        List<AssignedCertificate> certs = certService.findCertificatesOfEmployee(empId);
        if (certs.isEmpty()) {
            out.info("No certificates assigned.");
            return;
        }

        out.employeeCertificateTable(certs);

        System.out.print("Certificate ID: ");
        int certId;
        try {
            certId = in.readInt("Invalid certificate ID.");
        } catch (IllegalArgumentException e) {
            out.error(e.getMessage());
            return;
        }

        AssignedCertificate existing = certs.stream()
                .filter(c -> c.getCertificateId() == certId)
                .findFirst()
                .orElse(null);

        if (existing == null) {
            out.error("Certificate not assigned.");
            return;
        }

        System.out.print("Assigned date [" + existing.getValidFrom() + "] (yyyy-mm-dd): ");
        String date = in.readLine().trim();
        if (date.isBlank()) date = existing.getValidFrom();

        System.out.print("Certificate number [" + (existing.getCertificateNumber() == null ? "-" : existing.getCertificateNumber()) + "]: ");
        String certNo = in.readLine().trim();
        if (certNo.isBlank()) certNo = existing.getCertificateNumber();

        System.out.print("Status [" + existing.getStatus() + "] (ACTIVE/EXPIRED/REVOKED...): ");
        String status = in.readLine().trim();
        if (status.isBlank()) status = existing.getStatus();

        try {
            certService.updateCertificateAssignment(empId, certId, date, certNo, status);
            out.info("Certificate assignment updated.");
        } catch (RuntimeException e) {
            out.error(e.getMessage());
        }
    }
}