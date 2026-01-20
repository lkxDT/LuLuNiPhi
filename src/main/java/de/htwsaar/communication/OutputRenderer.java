package de.htwsaar.communication;

import de.htwsaar.domain.Employee;
import de.htwsaar.domain.Skill;
import de.htwsaar.domain.Certificate;
import de.htwsaar.util.AnsiColorUtil;
import java.util.List;

public class OutputRenderer {

    public void section(String title) {
        System.out.println("==================================================");
        System.out.println(title);
        System.out.println("==================================================");
    }

    public void info(String msg) {
        System.out.println(AnsiColorUtil.colorizeStatus(AnsiColorUtil.GREEN, 6)+ "[INFO] " + msg);
    }

    public void error(String msg) {
        System.out.println(AnsiColorUtil.colorizeStatus(AnsiColorUtil.RED, 7)+ "[ERROR] " + msg);
    }

    public void employeeTable(List<Employee> employees) {
        if (employees == null || employees.isEmpty()) {
            info("No employees found.");
            return;
        }

        System.out.println("--------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-3s | %-15s | %-15s | %-30s | %-12s | %-12s | %-5s%n",
                "ID", "First name", "Last name", "Email", "Role", "Status", "Hrs");
        System.out.println("--------------------------------------------------------------------------------------------------------------");

        for (Employee emp : employees) {
            String coloredStatus =
                    AnsiColorUtil.colorizeStatus(emp.getStatus(), 12);
            System.out.printf("%-3d | %-15s | %-15s | %-30s | %-12s | %-12s | %-5d%n",
                    emp.getId(),
                    emp.getFirstName(),
                    emp.getLastName(),
                    emp.getEmail(),
                    emp.getRole(),
                    coloredStatus,
                    emp.getWorkingHours());
        }

        System.out.println("--------------------------------------------------------------------------------------------------------------");
    }

    public void skillTable(List<Skill> skills) {
        if (skills == null || skills.isEmpty()) {
            info("No skills found.");
            return;
        }

        System.out.println("--------------------------------------------------------------");
        System.out.printf("%-3s | %-25s | %-20s%n", "ID", "Skill name", "Category");
        System.out.println("--------------------------------------------------------------");

        for (Skill skill : skills) {
            System.out.printf("%-3d | %-25s | %-20s%n",
                    skill.getId(),
                    skill.getName(),
                    skill.getCategory());
        }

        System.out.println("--------------------------------------------------------------");
    }


    public void certificateTable(List<Certificate> certs) {
        if (certs == null || certs.isEmpty()) {
            info("No certificates found.");
            return;
        }

        System.out.println("---------------------------------------------------------------------------------------------");
        System.out.printf("%-3s | %-25s | %-12s | %-10s | %-10s | %-15s%n",
                "ID", "Name", "Category", "Issuer", "Renewal", "Cycle (months)");
        System.out.println("---------------------------------------------------------------------------------------------");

        for (Certificate cert : certs) {
            String renewal = cert.isRequiresRenewal() ? "YES" : "NO";
            String cycle = cert.getRenewalCycleMonths() != null ? cert.getRenewalCycleMonths().toString() : "-";

            System.out.printf("%-3d | %-25s | %-12s | %-10s | %-10s | %-15s%n",
                    cert.getId(),
                    cert.getName(),
                    cert.getCategory(),
                    cert.getIssuerType(),
                    renewal,
                    cycle);
        }

        System.out.println("---------------------------------------------------------------------------------------------");
    }
}
