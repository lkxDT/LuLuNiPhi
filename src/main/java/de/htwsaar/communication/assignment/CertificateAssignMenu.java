package de.htwsaar.communication.assignment;

import de.htwsaar.application.CertificateService;
import de.htwsaar.application.EmployeeService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;

/**
 * Menu for assigning certificates to an employee.
 */
public class CertificateAssignMenu {
    /**
     * Utility class constructor.
     * Prevents instantiation.
     */
    private CertificateAssignMenu() {}

    /**
     * Displays the menu to assign a certificate to an employee.
     *
     * @param out         OutputRenderer for console output
     * @param in          InputReader for user input
     * @param empService  Service for employee operations
     * @param certService Service for certificate operations
     */
    protected static void show(OutputRenderer out,
                               InputReader in,
                               EmployeeService empService,
                               CertificateService certService) {

        System.out.print("Employee ID: ");
        int empId;
        try {
            empId = Integer.parseInt(in.readLine().trim());
        } catch (NumberFormatException e) {
            out.error("Invalid employee ID.");
            return;
        }

        if (empService.findEmployeeByID(empId).isEmpty()) {
            out.error("Employee with ID " + empId + " not found.");
            return;
        }

        out.info("Available certificates:");
        out.certificateTable(certService.findAllCertificates());

        System.out.print("Certificate ID: ");
        int certId;
        try {
            certId = Integer.parseInt(in.readLine().trim());
        } catch (NumberFormatException e) {
            out.error("Invalid certificate ID.");
            return;
        }

        if (certService.findCertificateById(certId).isEmpty()) {
            out.error("Certificate with ID " + certId + " not found.");
            return;
        }

        System.out.print("Assigned date (yyyy-mm-dd): ");
        String assignedDate = in.readLine().trim();

        try {
            certService.assignCertificateToEmployee(empId, certId, assignedDate);
            out.info("Certificate assigned.");
        } catch (RuntimeException e) {
            out.error(e.getMessage());
        }
    }
}