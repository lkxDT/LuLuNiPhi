package de.htwsaar.communication.assignment;

import de.htwsaar.application.CertificateService;
import de.htwsaar.application.EmployeeService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;

/**
 * Menu for displaying certificates assigned to an employee.
 */
public class EmployeeCertificateShowMenu {

    /**
     * Utility class constructor.
     * Prevents instantiation.
     */
    private EmployeeCertificateShowMenu() {}

    /**
     * Displays the certificates assigned to a specific employee.
     *
     * @param out         OutputRenderer for console output
     * @param in          InputReader for user input
     * @param empService  Service for employee operations
     * @param certService Service for certificate operations
     */
    public static void show(OutputRenderer out,
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

        var empOpt = empService.findEmployeeByID(empId);
        if (empOpt.isEmpty()) {
            out.error("Employee with ID " + empId + " not found.");
            return;
        }

        out.info("Certificates of " + empOpt.get().getFirstName() + " " + empOpt.get().getLastName() + ":");
        out.employeeCertificateTable(certService.findCertificatesOfEmployee(empId));
    }
}