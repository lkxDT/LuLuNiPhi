package de.htwsaar.communication;

import java.util.List;

import de.htwsaar.domain.AssignedSkill;
import de.htwsaar.domain.Certificate;
import de.htwsaar.domain.Employee;
import de.htwsaar.domain.Skill;
import de.htwsaar.util.AnsiColorUtil;
import de.htwsaar.domain.AssignedCertificate;

public class OutputRenderer {

    public void section(String title) {
        System.out.println("==================================================");
        System.out.println(AnsiColorUtil.GREEN + title + AnsiColorUtil.RESET);
        System.out.println("==================================================");
    }

    public void info(String msg) {
        System.out.println(AnsiColorUtil.YELLOW + "[INFO] " + AnsiColorUtil.RESET + msg);
    }

    public void error(String msg) {
        System.out.println(AnsiColorUtil.RED + "[ERROR] "+ AnsiColorUtil.RESET + msg);
    }

    public void warning(String msg) {
        System.out.println(AnsiColorUtil.PURPLE + "[WARNING] "+ AnsiColorUtil.RESET + msg);
    }

    public void printStartupBanner() {
        String CYAN = "\u001B[36m";
        String RESET = "\u001B[0m";

        System.out.println(CYAN + "========================================");
        System.out.println("            LuLuNiPhi 2.0");
        System.out.println("      Employee Management System");
        System.out.println("========================================" + RESET);
        System.out.println();
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
            String cycle = cert.getRenewalCycleMonths() != null
                    ? cert.getRenewalCycleMonths().toString()
                    : "-";

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

    public void assignedSkillTable(List<AssignedSkill> skills) {
        if (skills.isEmpty()) {
            info("No skills assigned.");
            return;
        }

        System.out.println("-------------------------------------------------------------------------------------");
        System.out.printf("%-3s | %-25s | %-20s | %-5s | %-10s%n",
                "ID", "Skill", "Category", "Lvl", "Since");
        System.out.println("-------------------------------------------------------------------------------------");

        for (AssignedSkill s : skills) {
            System.out.printf("%-3d | %-25s | %-20s | %-5d | %-10s%n",
                    s.getSkillId(),
                    s.getName(),
                    s.getCategory(),
                    s.getLevel(),
                    s.getSinceDate());
        }

        System.out.println("-------------------------------------------------------------------------------------");
    }
    public void employeeCertificateTable(List<AssignedCertificate> certs) {
        if (certs == null || certs.isEmpty()) {
            info("No certificates assigned.");
            return;
        }

        System.out.println("---------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-3s | %-25s | %-12s | %-10s | %-10s | %-12s | %-8s%n",
                "ID", "Name", "Category", "Valid from", "Valid until", "Cert-No", "Status");
        System.out.println("---------------------------------------------------------------------------------------------------------------");

        for (AssignedCertificate c : certs) {
            String validUntil = (c.getValidUntil() == null ? "-" : c.getValidUntil());
            String certNo = (c.getCertificateNumber() == null ? "-" : c.getCertificateNumber());

            System.out.printf("%-3d | %-25s | %-12s | %-10s | %-10s | %-12s | %-8s%n",
                    c.getCertificateId(),
                    c.getName(),
                    c.getCategory(),
                    c.getValidFrom(),
                    validUntil,
                    certNo,
                    c.getStatus());
        }

        System.out.println("---------------------------------------------------------------------------------------------------------------");
    }
}
