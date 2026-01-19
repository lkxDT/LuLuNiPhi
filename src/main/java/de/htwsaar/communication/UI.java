package de.htwsaar.communication;

import java.util.List;
import java.util.Scanner;

import de.htwsaar.application.CertificateService;
import de.htwsaar.application.EmployeeService;
import de.htwsaar.application.SkillService;
import de.htwsaar.domain.AssignedSkill;
import de.htwsaar.domain.Certificate;
import de.htwsaar.domain.Employee;
import de.htwsaar.domain.Skill;
import de.htwsaar.util.AnsiColorUtil;
import de.htwsaar.domain.Employee;

public class UI {
    private final Scanner inputScanner;
    private final EmployeeService empService;
    private final SkillService skillService;
    private final CertificateService certificateService;
    private boolean shouldExit;

    public UI(EmployeeService ems, SkillService sks, CertificateService ces) {
        this(new Scanner(System.in), ems, sks, ces);
    }

    public UI(Scanner inputScanner, EmployeeService ems, SkillService sks, CertificateService ces) {
        this.empService = ems;
        this.skillService = sks;
        this.certificateService = ces;
        this.inputScanner = inputScanner;
        this.shouldExit = false;
    }

    /* =======================
       Layout helpers
       ======================= */

    private void printSection(String title) {
        System.out.println("==================================================");
        System.out.println(title);
        System.out.println("==================================================");
    }

    private void printInfo(String msg) {
        System.out.println("[INFO] " + msg);
    }

    private void printError(String msg) {
        System.out.println("[ERROR] " + msg);
    }

    private void printEmployeeTable(List<Employee> employees) {
        if (employees == null || employees.isEmpty()) {
            printInfo("No employees found.");
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

    private void printSkillTable(List<Skill> skills) {
        if (skills == null || skills.isEmpty()) {
            printInfo("No skills found.");
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

    private void printCertificateTable(List<Certificate> certs) {
        if (certs == null || certs.isEmpty()) {
            printInfo("No certificates found.");
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

    /* =======================
       Input helper
       ======================= */

    public int getInputNumber(int max) {
        while (true) {
            if (!inputScanner.hasNext()) {
                return -1;
            }

            String input = inputScanner.nextLine();

            if (input.contains(".") || input.contains(",")) {
                printError("Please do not use decimals or commas.");
                continue;
            }

            try {
                int numerizedInput = Integer.parseInt(input);

                if (numerizedInput < 1 || numerizedInput > max) {
                    printError("Please enter a number between 1 and " + max + ".");
                    continue;
                }

                return numerizedInput;

            } catch (NumberFormatException e) {
                printError("Invalid input. Please enter a whole number.");
            }
        }
    }

    /* =======================
       Main menu
       ======================= */

    public void startMainMenu() {
        shouldExit = false;
        printSection("Main Menu");
        System.out.println("1. Employees");
        System.out.println("2. Skills");
        System.out.println("3. Certificates");
        System.out.println("4. Exit");

        try {
            switch (this.getInputNumber(4)) {
                case 1 -> employeeMenu();
                case 2 -> skillsMenu();
                case 3 -> certificatesMenu();
                case 4 -> {
                    printInfo("Exiting the program...");
                    shouldExit = true;
                }
            }
        } catch (Exception e) {
            printError(e.getMessage());
        }

        if (!shouldExit) startMainMenu();
    }

    public void showStartupBanner() {
        String CYAN = "\u001B[36m";
        String RESET = "\u001B[0m";

        System.out.println(CYAN + "========================================");
        System.out.println("        LuLuNiPhi 2.0");
        System.out.println("   Employee Management System");
        System.out.println("========================================" + RESET);
        System.out.println();
    }

    /* =======================
       Employees
       ======================= */

    public void employeeMenu() {
        printSection("Employees");
        System.out.println("1. Show all");
        System.out.println("2. Add");
        System.out.println("3. Search");
        System.out.println("4. Change");
        System.out.println("5. Employee Skills");
        System.out.println("6. Return to main");

        switch (this.getInputNumber(6)) {
            case 1 -> printEmployeeTable(empService.findAllEmployees());

            case 2 -> {
                printSection("Add Employee");

                System.out.print("First name: ");
                String firstName = inputScanner.nextLine();

                System.out.print("Last name: ");
                String lastName = inputScanner.nextLine();

                System.out.print("Role: ");
                String role = inputScanner.nextLine();

                System.out.print("Status: ");
                String status = inputScanner.nextLine();

                System.out.print("Hire date (yyyy-mm-dd): ");
                String hireDate = inputScanner.nextLine();

                System.out.print("Working hours per week: ");
                int workingHours;
                try {
                    workingHours = Integer.parseInt(inputScanner.nextLine().trim());
                } catch (NumberFormatException e) {
                    printError("Invalid working hours.");
                    break;
                }

                try {
                    Employee created = empService.createEmployee(firstName, lastName, role, status, hireDate, workingHours);
                    printInfo("Employee successfully created: " + created.getEmail());
                } catch (IllegalArgumentException e) {
                    // Eingabefehler (User)
                    printError(e.getMessage());
                } catch (RuntimeException e) {
                    // Systemfehler (DB/Repo etc.)
                    printError("Employee creation failed: " + e.getMessage());
                }
            }

            case 3 -> employeeSearchMenu();

            case 4 -> {
                printSection("Change Employee");

                System.out.print("Enter employee ID to change: ");
                int id;
                try {
                    id = Integer.parseInt(inputScanner.nextLine().trim());
                } catch (NumberFormatException e) {
                    printError("Invalid ID. Please enter a whole number.");
                    break;
                }

                empService.findEmployeeByID(id).ifPresentOrElse(emp -> {

                    printInfo("Press Enter to keep current value.");

                    System.out.print("First name [" + emp.getFirstName() + "]: ");
                    String firstName = inputScanner.nextLine();
                    if (firstName.isBlank()) firstName = emp.getFirstName();

                    System.out.print("Last name  [" + emp.getLastName() + "]: ");
                    String lastName = inputScanner.nextLine();
                    if (lastName.isBlank()) lastName = emp.getLastName();

                    System.out.print("Role       [" + emp.getRole() + "]: ");
                    String role = inputScanner.nextLine();
                    if (role.isBlank()) role = emp.getRole();

                    System.out.print("Status     [" + emp.getStatus() + "]: ");
                    String status = inputScanner.nextLine();
                    if (status.isBlank()) status = emp.getStatus();

                    System.out.print("Hire date  [" + emp.getHireDate() + "] (yyyy-mm-dd): ");
                    String hireDate = inputScanner.nextLine();
                    if (hireDate.isBlank()) hireDate = emp.getHireDate();

                    Integer workingHours;
                    System.out.print("Working hours [" + emp.getWorkingHours() + "]: ");
                    String whStr = inputScanner.nextLine().trim();
                    if (whStr.isBlank()) {
                        workingHours = emp.getWorkingHours();
                    } else {
                        try {
                            workingHours = Integer.parseInt(whStr);
                        } catch (NumberFormatException e) {
                            printError("Invalid working hours. Update cancelled.");
                            return;
                        }
                    }

                    String email = emp.getEmail();
                    if (!firstName.equals(emp.getFirstName()) || !lastName.equals(emp.getLastName())) {
                        System.out.print("Regenerate email based on the new name? (y/n): ");
                        String regen = inputScanner.nextLine().trim().toLowerCase();
                        if (regen.equals("y") || regen.equals("yes")) {
                            email = empService.generateEmailForUpdate(firstName, lastName, emp.getId());
                        }
                    }

                    Employee updated = new Employee(
                            emp.getId(),
                            firstName,
                            lastName,
                            email,
                            role,
                            status,
                            hireDate,
                            workingHours
                    );

                    try {
                        empService.updateEmployee(updated);
                        printInfo("Employee updated successfully.");
                    } catch (RuntimeException e) {
                        printError("Employee update failed: " + e.getMessage());
                    }

                }, () -> printError("Employee with ID " + id + " not found."));
            }

            case 5 -> employeeSkillsMenu();

            case 6 -> shouldExit = true;
        }

        if (shouldExit) startMainMenu();
        else employeeMenu();
    }

    public void employeeSearchMenu() {
        printSection("Search Employee");
        System.out.println("1. By ID");
        System.out.println("2. By last name");
        System.out.println("3. Return to employees");

        switch (this.getInputNumber(3)) {
            case 1 -> {
                System.out.print("Enter ID: ");
                int id;
                try {
                    id = Integer.parseInt(inputScanner.nextLine().trim());
                } catch (NumberFormatException e) {
                    printError("Invalid ID.");
                    return;
                }

                empService.findEmployeeByID(id).ifPresentOrElse(
                        employee -> printEmployeeTable(List.of(employee)),
                        () -> printError("Employee with ID " + id + " not found.")
                );
            }

            case 2 -> {
                System.out.print("Enter last name (or part of it): ");
                String lastName = inputScanner.nextLine().trim();
                printEmployeeTable(empService.findEmployeesByLastName(lastName));
            }

            case 3 -> employeeMenu();
        }
    }

    /* =======================
       Employee Skills
       ======================= */

    public void employeeSkillsMenu() {
        printSection("Employee Skills");
        System.out.println("1. Assign skill to employee");
        System.out.println("2. Show skills of employee");
        System.out.println("3. Update skill assignment");
        System.out.println("4. Return to employees");

        switch (this.getInputNumber(4)) {
            case 1 -> assignSkillToEmployee();
            case 2 -> showSkillsOfEmployee();
            case 3 -> updateSkillAssignment();
            case 4 -> employeeMenu();
        }
    }

    private void assignSkillToEmployee() {
        printSection("Assign Skill to Employee");

        System.out.print("Enter employee ID: ");
        int empId;
        try {
            empId = Integer.parseInt(inputScanner.nextLine().trim());
        } catch (NumberFormatException e) {
            printError("Invalid employee ID.");
            return;
        }

        if (empService.findEmployeeByID(empId).isEmpty()) {
            printError("Employee with ID " + empId + " not found.");
            return;
        }

        printInfo("Available skills:");
        printSkillTable(skillService.findAllSkills());

        System.out.print("Enter skill ID: ");
        int skillId;
        try {
            skillId = Integer.parseInt(inputScanner.nextLine().trim());
        } catch (NumberFormatException e) {
            printError("Invalid skill ID.");
            return;
        }

        if (skillService.findSkillByID(skillId).isEmpty()) {
            printError("Skill with ID " + skillId + " not found.");
            return;
        }

        System.out.print("Enter level (1-5): ");
        int level;
        try {
            level = Integer.parseInt(inputScanner.nextLine().trim());
        } catch (NumberFormatException e) {
            printError("Invalid level.");
            return;
        }

        if (level < 1 || level > 5) {
            printError("Level must be between 1 and 5.");
            return;
        }

        System.out.print("Since date (yyyy-mm-dd): ");
        String sinceDate = inputScanner.nextLine().trim();
        if (sinceDate.isBlank()) {
            printError("Since date must not be empty.");
            return;
        }

        try {
            skillService.assignSkill(empId, skillId, level, sinceDate);
            printInfo("Skill successfully assigned to employee.");
        } catch (RuntimeException e) {
            printError("Assignment failed: " + e.getMessage());
        }
    }

    private void updateSkillAssignment() {
        printSection("Update Skill Assignment");

        System.out.print("Enter employee ID: ");
        int empId;
        try {
            empId = Integer.parseInt(inputScanner.nextLine().trim());
        } catch (NumberFormatException e) {
            printError("Invalid employee ID.");
            return;
        }

        if (empService.findEmployeeByID(empId).isEmpty()) {
            printError("Employee with ID " + empId + " not found.");
            return;
        }

        // Show current skills for the employee (so user can pick a skillId)
        List<AssignedSkill> current = skillService.findSkillsOfEmployee(empId);
        if (current.isEmpty()) {
            printInfo("No skills assigned to this employee.");
            return;
        }

        System.out.println("-------------------------------------------------------------------------------------");
        System.out.printf("%-3s | %-25s | %-20s | %-5s | %-10s%n",
                "ID", "Skill", "Category", "Lvl", "Since");
        System.out.println("-------------------------------------------------------------------------------------");
        for (AssignedSkill s : current) {
            System.out.printf("%-3d | %-25s | %-20s | %-5d | %-10s%n",
                    s.getSkillId(), s.getName(), s.getCategory(), s.getLevel(), s.getSinceDate());
        }
        System.out.println("-------------------------------------------------------------------------------------");

        System.out.print("Enter skill ID to update: ");
        int skillId;
        try {
            skillId = Integer.parseInt(inputScanner.nextLine().trim());
        } catch (NumberFormatException e) {
            printError("Invalid skill ID.");
            return;
        }

        AssignedSkill existing = null;
        for (AssignedSkill s : current) {
            if (s.getSkillId() == skillId) {
                existing = s;
                break;
            }
        }
        if (existing == null) {
            printError("This skill is not assigned to the employee.");
            return;
        }

        System.out.println("Press Enter to keep current value.");

        // Level
        int newLevel = existing.getLevel();
        System.out.print("Level [" + existing.getLevel() + "] (1-5): ");
        String lvlStr = inputScanner.nextLine().trim();
        if (!lvlStr.isBlank()) {
            try {
                newLevel = Integer.parseInt(lvlStr);
            } catch (NumberFormatException e) {
                printError("Invalid level.");
                return;
            }
        }

        // Since date
        String newSince = existing.getSinceDate();
        System.out.print("Since date [" + existing.getSinceDate() + "] (yyyy-mm-dd): ");
        String sinceStr = inputScanner.nextLine().trim();
        if (!sinceStr.isBlank()) {
            newSince = sinceStr;
        }

        try {
            skillService.updateSkillAssignment(empId, skillId, newLevel, newSince);
            printInfo("Skill assignment updated successfully.");
        } catch (RuntimeException e) {
            printError("Update failed: " + e.getMessage());
        }
    }

    private void showSkillsOfEmployee() {
        printSection("Skills of Employee");

        System.out.print("Enter employee ID: ");
        int empId;
        try {
            empId = Integer.parseInt(inputScanner.nextLine().trim());
        } catch (NumberFormatException e) {
            printError("Invalid employee ID.");
            return;
        }

        if (empService.findEmployeeByID(empId).isEmpty()) {
            printError("Employee with ID " + empId + " not found.");
            return;
        }

        List<AssignedSkill> skills = skillService.findSkillsOfEmployee(empId);
        if (skills.isEmpty()) {
            printInfo("No skills assigned to this employee.");
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

    /* =======================
       Skills
       ======================= */

    public void skillsMenu() {
        printSection("Skills");
        System.out.println("1. Show all");
        System.out.println("2. Add");
        System.out.println("3. Search");
        System.out.println("4. Change");
        System.out.println("5. Return to main");

        switch (this.getInputNumber(5)) {
            case 1 -> printSkillTable(skillService.findAllSkills());

            case 2 -> {
                printSection("Add Skill");

                System.out.print("Skill name: ");
                String name = inputScanner.nextLine();

                System.out.print("Category: ");
                String category = inputScanner.nextLine();

                try {
                    Skill created = skillService.createSkill(name, category);
                    printInfo("Skill successfully created.");
                    printSkillTable(List.of(created));
                } catch (RuntimeException e) {
                    printError("Skill creation failed: " + e.getMessage());
                }
            }

            case 3 -> skillSearchMenu();

            case 4 -> {
                printSection("Change Skill");

                System.out.print("Enter skill ID to change: ");
                int id;
                try {
                    id = Integer.parseInt(inputScanner.nextLine().trim());
                } catch (NumberFormatException e) {
                    printError("Invalid ID. Please enter a whole number.");
                    return;
                }

                skillService.findSkillByID(id).ifPresentOrElse(skill -> {

                    printInfo("Press Enter to keep current value.");

                    System.out.print("Skill name [" + skill.getName() + "]: ");
                    String newName = inputScanner.nextLine();
                    if (newName.isBlank()) newName = skill.getName();

                    System.out.print("Category  [" + skill.getCategory() + "]: ");
                    String newCategory = inputScanner.nextLine();
                    if (newCategory.isBlank()) newCategory = skill.getCategory();

                    Skill updated = new Skill(skill.getId(), newName.trim(), newCategory.trim());

                    try {
                        skillService.updateSkill(updated);
                        printInfo("Skill updated successfully.");
                    } catch (RuntimeException e) {
                        printError("Skill update failed: " + e.getMessage());
                    }

                }, () -> printError("Skill with ID " + id + " not found."));
            }

            case 5 -> shouldExit = true;
        }

        if (shouldExit) startMainMenu();
        else skillsMenu();
    }

    public void skillSearchMenu() {
        printSection("Search Skill");
        System.out.println("1. By ID");
        System.out.println("2. By name");
        System.out.println("3. Return to skills");

        switch (this.getInputNumber(3)) {
            case 1 -> {
                System.out.print("Enter ID: ");
                int id;
                try {
                    id = Integer.parseInt(inputScanner.nextLine().trim());
                } catch (NumberFormatException e) {
                    printError("Invalid ID.");
                    return;
                }

                skillService.findSkillByID(id).ifPresentOrElse(
                        skill -> printSkillTable(List.of(skill)),
                        () -> printError("Skill with ID " + id + " not found.")
                );
            }

            case 2 -> {
                System.out.print("Enter name: ");
                String name = inputScanner.nextLine().trim();

                skillService.findSkillByName(name).ifPresentOrElse(
                        skill -> printSkillTable(List.of(skill)),
                        () -> printError("Skill with name '" + name + "' not found.")
                );
            }

            case 3 -> skillsMenu();
        }
    }

    /* =======================
       Certificates
       ======================= */

    public void certificatesMenu() {
        printSection("Certificates");
        System.out.println("1. Show all");
        System.out.println("2. Show details (by ID)");
        System.out.println("3. Add");
        System.out.println("4. Change");
        System.out.println("5. Return to main");

        switch (this.getInputNumber(5)) {
            case 1 -> printCertificateTable(certificateService.findAllCertificates());

            case 2 -> {
                System.out.print("Enter certificate ID: ");
                int id;
                try {
                    id = Integer.parseInt(inputScanner.nextLine().trim());
                } catch (NumberFormatException e) {
                    printError("Invalid ID. Please enter a whole number.");
                    return;
                }

                certificateService.findCertificateById(id).ifPresentOrElse(cert -> {
                    System.out.println("--------------------------------------------------");
                    System.out.println("Certificate Details");
                    System.out.println("--------------------------------------------------");
                    System.out.println("ID: " + cert.getId());
                    System.out.println("Name: " + cert.getName());
                    System.out.println("Category: " + cert.getCategory());
                    System.out.println("Issuer Type: " + cert.getIssuerType());
                    System.out.println("Requires Renewal: " + (cert.isRequiresRenewal() ? "YES" : "NO"));
                    System.out.println("Renewal Cycle (months): " +
                            (cert.getRenewalCycleMonths() != null ? cert.getRenewalCycleMonths() : "-"));
                    System.out.println("Description: " +
                            (cert.getDescription() != null && !cert.getDescription().isBlank()
                                    ? cert.getDescription()
                                    : "-"));
                    System.out.println("--------------------------------------------------");
                }, () -> printError("Certificate with ID " + id + " not found."));
            }

            case 3 -> {
                printSection("Add Certificate");

                System.out.print("Certificate name: ");
                String name = inputScanner.nextLine();

                System.out.print("Category (e.g. Quality, Security, Regulatory): ");
                String category = inputScanner.nextLine();

                System.out.print("Issuer type (e.g. ISO, IEC, Regulation, Audit): ");
                String issuerType = inputScanner.nextLine();

                System.out.print("Requires renewal? (y/n): ");
                String renewalInput = inputScanner.nextLine().trim().toLowerCase();
                boolean requiresRenewal = renewalInput.equals("y") || renewalInput.equals("yes");

                Integer renewalCycleMonths = null;
                if (requiresRenewal) {
                    System.out.print("Renewal cycle in months (e.g. 12, 24, 36): ");
                    String monthsStr = inputScanner.nextLine().trim();
                    try {
                        renewalCycleMonths = Integer.parseInt(monthsStr);
                    } catch (NumberFormatException e) {
                        printError("Invalid number. Certificate creation cancelled.");
                        return;
                    }
                }

                System.out.print("Description (optional, press Enter to skip): ");
                String description = inputScanner.nextLine();

                try {
                    Certificate created = certificateService.createCertificate(
                            name, category, issuerType, requiresRenewal, renewalCycleMonths, description
                    );
                    printInfo("Certificate successfully created.");
                    System.out.println(created);
                } catch (RuntimeException e) {
                    printError("Certificate creation failed: " + e.getMessage());
                }
            }

            case 4 -> {
                System.out.print("Enter certificate ID to change: ");
                int id;

                try {
                    id = Integer.parseInt(inputScanner.nextLine().trim());
                } catch (NumberFormatException e) {
                    printError("Invalid ID.");
                    return;
                }

                certificateService.findCertificateById(id).ifPresentOrElse(cert -> {

                    printInfo("Press Enter to keep current value.");

                    System.out.print("Name [" + cert.getName() + "]: ");
                    String name = inputScanner.nextLine();
                    if (!name.isBlank()) cert = new Certificate(
                            cert.getId(),
                            name,
                            cert.getCategory(),
                            cert.getIssuerType(),
                            cert.isRequiresRenewal(),
                            cert.getRenewalCycleMonths(),
                            cert.getDescription()
                    );

                    System.out.print("Category [" + cert.getCategory() + "]: ");
                    String category = inputScanner.nextLine();
                    if (!category.isBlank()) cert = new Certificate(
                            cert.getId(),
                            cert.getName(),
                            category,
                            cert.getIssuerType(),
                            cert.isRequiresRenewal(),
                            cert.getRenewalCycleMonths(),
                            cert.getDescription()
                    );

                    System.out.print("Description [" +
                            (cert.getDescription() != null ? cert.getDescription() : "-") + "]: ");
                    String desc = inputScanner.nextLine();
                    if (!desc.isBlank()) cert = new Certificate(
                            cert.getId(),
                            cert.getName(),
                            cert.getCategory(),
                            cert.getIssuerType(),
                            cert.isRequiresRenewal(),
                            cert.getRenewalCycleMonths(),
                            desc
                    );

                    try {
                        certificateService.updateCertificate(cert);
                        printInfo("Certificate updated successfully.");
                    } catch (RuntimeException e) {
                        printError("Certificate update failed: " + e.getMessage());
                    }

                }, () -> printError("Certificate not found."));
            }

            case 5 -> shouldExit = true;
        }

        if (shouldExit) startMainMenu();
        else certificatesMenu();
    }
}