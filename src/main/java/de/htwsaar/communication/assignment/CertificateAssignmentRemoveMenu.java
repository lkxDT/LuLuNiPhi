package de.htwsaar.communication.assignment;

import de.htwsaar.application.CertificateService;
import de.htwsaar.application.EmployeeService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;
import de.htwsaar.domain.AssignedCertificate;

import java.util.List;

public class CertificateAssignmentRemoveMenu {
    private CertificateAssignmentRemoveMenu() {}

    public static void show(OutputRenderer out, InputReader in, EmployeeService empService, CertificateService certService) {
        out.section("Remove Certificate Assignment");

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

        System.out.print("Certificate ID to remove: ");
        int certId;
        try {
            certId = in.readInt("Invalid certificate ID.");
        } catch (IllegalArgumentException e) {
            out.error(e.getMessage());
            return;
        }

        boolean exists = certs.stream().anyMatch(c -> c.getCertificateId() == certId);
        if (!exists) {
            out.error("Certificate not assigned.");
            return;
        }

        System.out.print("Are you sure you want to remove this certificate assignment? (y/N): ");
        String confirm = in.readLine();
        if (!confirm.equalsIgnoreCase("y")) {
            out.info("Removal canceled.");
            return;
        }

        try {
            certService.removeCertificateFromEmployee(empId, certId);
            out.info("Certificate assignment removed.");
        } catch (RuntimeException e) {
            out.error(e.getMessage());
        }
    }
}