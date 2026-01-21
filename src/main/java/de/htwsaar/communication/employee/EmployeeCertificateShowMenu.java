package de.htwsaar.communication.employee;

import de.htwsaar.application.CertificateService;
import de.htwsaar.application.EmployeeService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;

public class EmployeeCertificateShowMenu {

    private EmployeeCertificateShowMenu() {}

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