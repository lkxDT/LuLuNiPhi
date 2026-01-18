
package de.htwsaar.communication;

import java.util.Scanner;
import de.htwsaar.application.*;
import de.htwsaar.domain.Employee;
import de.htwsaar.domain.Skill;
import de.htwsaar.application.CertificateService;
import de.htwsaar.domain.Certificate;


public class UI {
    private final Scanner inputScanner;
    private final EmployeeService empService;
    private final SkillService skillService;
    private boolean shouldExit;
    private final CertificateService certificateService;

    //Standat Scanner for usage
    public UI(EmployeeService ems, SkillService sks, CertificateService ces) {
        this(new Scanner(System.in), ems, sks, ces);
    }

    /**
     * Constructor for the UI Object
     * @param inputScanner Scanner for Input (for tests)
     * @param ems EmployeeService abstraction
     * @param sks SkillService abstraction
     */
    public UI(Scanner inputScanner, EmployeeService ems, SkillService sks, CertificateService ces) {
        this.empService = ems;
        this.skillService = sks;
        this.certificateService = ces;
        this.inputScanner = inputScanner;
        this.shouldExit = false;
    }

    public int getInputNumber(int max) {
        while (true) {
            if (!inputScanner.hasNext()) {
                return -1;
            }

            String input = inputScanner.nextLine();

            if (input.contains(".") || input.contains(",")) {
                System.out.println("Please do not use decimals or commas.");
                continue;
            }

            try {
                int numerizedInput = Integer.parseInt(input);

                if (numerizedInput < 1 || numerizedInput > max) {
                    System.out.println("Please enter a number between 1 and " + max + ".");
                    continue;
                }

                return numerizedInput;

            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }
    }

    public void startMainMenu() {
        shouldExit = false;
        System.out.println("Main Menu:\n" +
                "1. Employees\n" +
                "2. Skills\n" +
                "3. Certificates\n" +
                //"4. Projects\n" +
                "4. Exit");

        try {
            switch (this.getInputNumber(4)) {
                case 1 -> employeeMenu();
                case 2 -> skillsMenu();
                case 3 -> certificatesMenu();
                //case 4 -> projectsMenu();
                case 4 -> {
                    System.out.println("Exiting the Program...");
                    shouldExit = true;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        if(!shouldExit) startMainMenu();
    }

    public void employeeMenu() {
        System.out.println("Employees Menu:\n" +
                "1. Show all\n" +
                "2. Add\n" +
                "3. Search\n" +
                "4. Change\n" +
                "5. Return to main\n");

        switch (this.getInputNumber(5)) {
            case 1 -> {
                var employees = empService.findAllEmployees();

                if (employees.isEmpty()) {
                    System.out.println("No employees found.");
                    return;
                }

                System.out.println(
                        "--------------------------------------------------------------------------------------------------------------");
                System.out.printf(
                        "%-3s | %-15s | %-15s | %-30s | %-12s | %-12s | %-5s%n",
                        "ID", "First name", "Last name", "Email", "Role", "Status", "Hrs");
                System.out.println(
                        "--------------------------------------------------------------------------------------------------------------");

                for (Employee emp : employees) {
                    System.out.printf(
                            "%-3d | %-15s | %-15s | %-30s | %-12s | %-12s | %-5d%n",
                            emp.getId(),
                            emp.getFirstName(),
                            emp.getLastName(),
                            emp.getEmail(),
                            emp.getRole(),
                            emp.getStatus(),
                            emp.getWorkingHours()
                    );
                }

                System.out.println(
                        "--------------------------------------------------------------------------------------------------------------");

            }
            case 2 -> {
                String firstName, lastName, role, status, hireDate;
                int workingHours;

                System.out.print("New Employee: \nPlease enter firstname: ");
                firstName = inputScanner.nextLine();

                System.out.print("New Employee: \nPlease enter lastname: ");
                lastName = inputScanner.nextLine();

                System.out.print("New Employee: \nPlease enter a role: ");
                role = inputScanner.nextLine();

                System.out.print("New Employee: \nPlease enter a status for the employee: ");
                status = inputScanner.nextLine();

                System.out.print("New Employee: \nPlease enter the date the employee was hired (yyyy-mm-dd): ");
                hireDate = inputScanner.nextLine();

                System.out.print("New Employee: \nPlease enter the amount of hours the employee works in a week: ");
                workingHours = Integer.parseInt(inputScanner.nextLine());

                try {
                    empService.createEmployee(firstName, lastName, role, status, hireDate, workingHours);
                    System.out.println("Employee successfully created");
                } catch (RuntimeException e) {
                    System.out.println("Employee creation failed: " + e.getMessage());
                }
            }
            case 3 -> employeeSearchMenu();
            case 4 -> {
                System.out.print("Enter employee ID to change: ");
                int id;
                try {
                    id = Integer.parseInt(inputScanner.nextLine().trim());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid ID. Please enter a whole number.");
                    return;
                }

                empService.findEmployeeByID(id).ifPresentOrElse(emp -> {

                    System.out.println("Press Enter to keep current value.");

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

                    Integer workingHours = null;
                    System.out.print("Working hours [" + emp.getWorkingHours() + "]: ");
                    String whStr = inputScanner.nextLine().trim();
                    if (whStr.isBlank()) {
                        workingHours = emp.getWorkingHours();
                    } else {
                        try {
                            workingHours = Integer.parseInt(whStr);
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid working hours. Update cancelled.");
                            return;
                        }
                    }

                    // Email: optional regenerieren (nur sinnvoll, wenn Name geändert wurde)
                    String email = emp.getEmail();
                    if (!firstName.equals(emp.getFirstName()) || !lastName.equals(emp.getLastName())) {
                        System.out.print("Regenerate email based on the new name? (y/n): ");
                        String regen = inputScanner.nextLine().trim().toLowerCase();
                        if (regen.equals("y") || regen.equals("yes")) {
                            // nutzt die Update-Unique-Logik im Service
                            email = empService.generateEmailForUpdate(firstName, lastName, emp.getId());
                            // siehe Hinweis unten!
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
                        System.out.println("Employee updated successfully.");
                    } catch (RuntimeException e) {
                        System.out.println("Employee update failed: " + e.getMessage());
                    }

                }, () -> System.out.println("Employee with ID " + id + " not found."));
            }
            case 5 -> shouldExit = true;
        }
        if(shouldExit) startMainMenu();
        else employeeMenu();
    }

    public void employeeSearchMenu() {
        System.out.println("Search Employee:\n" +
                "1. By ID\n" +
                "2. By last name\n" +
                "3. Return to employee\n");

        switch (this.getInputNumber(3)) {
            case 1 -> {
                System.out.println("Enter ID:\n");
                int id = inputScanner.nextInt();
                empService.findEmployeeByID(id).ifPresentOrElse(
                        employee -> System.out.println(employee.toString()),
                        () -> System.out.printf("Employee with ID %d not found\n", id)
                );
            }
            case 2 -> {
                System.out.print("Enter last name (or part of it): ");
                String lastName = inputScanner.nextLine().trim();

                var results = empService.findEmployeesByLastName(lastName);

                if (results.isEmpty()) {
                    System.out.println("No employees found for last name: " + lastName);
                    return;
                }

                System.out.println(
                        "--------------------------------------------------------------------------------------------------------------");
                System.out.printf(
                        "%-3s | %-15s | %-15s | %-30s | %-12s | %-12s | %-5s%n",
                        "ID", "First name", "Last name", "Email", "Role", "Status", "Hrs");
                System.out.println(
                        "--------------------------------------------------------------------------------------------------------------");

                for (Employee emp : results) {
                    System.out.printf(
                            "%-3d | %-15s | %-15s | %-30s | %-12s | %-12s | %-5d%n",
                            emp.getId(),
                            emp.getFirstName(),
                            emp.getLastName(),
                            emp.getEmail(),
                            emp.getRole(),
                            emp.getStatus(),
                            emp.getWorkingHours()
                    );
                }

                System.out.println(
                        "--------------------------------------------------------------------------------------------------------------");
            }
            case 3 -> employeeMenu();
        }
    }

    public void skillsMenu() {
        System.out.println("Skills Menu:\n" +
                "1. Show all\n" +
                "2. Add\n" +
                "3. Search\n" +
                "4. Change\n" +
                "5. Return to main\n");

        switch (this.getInputNumber(5)) {
            case 1 -> {
                for(Skill skill : skillService.findAllSkills()) {
                    System.out.println(skill);
                }
            }
            case 2 -> {
                System.out.print("New Skill:\nPlease enter skill name: ");
                String name = inputScanner.nextLine();

                System.out.print("Please enter category: ");
                String category = inputScanner.nextLine();

                try {
                    Skill created = skillService.createSkill(name, category);
                    System.out.println("Skill successfully created:");
                    System.out.println(created);
                } catch (RuntimeException e) {
                    System.out.println("Skill creation failed: " + e.getMessage());
                }
            }
            case 3 -> skillSearchMenu();
            case 4 -> {
                System.out.print("Enter skill ID to change: ");
                int id;
                try {
                    id = Integer.parseInt(inputScanner.nextLine().trim());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid ID. Please enter a whole number.");
                    return;
                }

                skillService.findSkillByID(id).ifPresentOrElse(skill -> {
                    System.out.println("Press Enter to keep current value.");

                    System.out.print("Skill name [" + skill.getName() + "]: ");
                    String newName = inputScanner.nextLine();
                    if (newName.isBlank()) newName = skill.getName();

                    System.out.print("Category  [" + skill.getCategory() + "]: ");
                    String newCategory = inputScanner.nextLine();
                    if (newCategory.isBlank()) newCategory = skill.getCategory();

                    Skill updated = new Skill(skill.getId(), newName.trim(), newCategory.trim());

                    try {
                        skillService.updateSkill(updated);
                        System.out.println("Skill updated successfully.");
                    } catch (RuntimeException e) {
                        System.out.println("Skill update failed: " + e.getMessage());
                    }

                }, () -> System.out.println("Skill with ID " + id + " not found."));
            }
            case 5 -> shouldExit = true;
        }
        if(shouldExit) startMainMenu();
        else skillsMenu();
    }

    public void skillSearchMenu() {
        System.out.println("Search Skill:\n" +
                "1. By ID\n" +
                "2. By name\n" +
                "3. Return to employee\n");

        switch (this.getInputNumber(3)) {
            case 1 -> {
                System.out.println("Enter ID:\n");
                int id = inputScanner.nextInt();
                skillService.findSkillByID(id).ifPresentOrElse(
                        skill -> System.out.println(skill.toString()),
                        () -> System.out.printf("Skill with ID %d not found\n", id)
                );
            }
            case 2 -> {
                System.out.println("Enter name:\n");
                String name = inputScanner.nextLine();
                skillService.findSkillByName(name).ifPresentOrElse(
                        skill -> System.out.println(skill.toString()),
                        () -> System.out.printf("Skill with name %s not found\n", name)
                );
            }
            case 3 -> skillsMenu();
        }
    }

    public void certificatesMenu() {
        System.out.println("Certificates Menu:\n" +
                "1. Show all\n" +
                "2. Show details (by ID)\n" +
                "3. Add\n" +
                "4. Change\n" +
                "5. Return to main \n");

        switch (this.getInputNumber(5)) {
            case 1 -> {
                var certs = certificateService.findAllCertificates();
                if (certs.isEmpty()) {
                    System.out.println("No certificates found.");
                } else {
                    System.out.println(
                            "---------------------------------------------------------------------------------------------");
                    System.out.printf(
                            "%-3s | %-25s | %-12s | %-10s | %-10s | %-15s%n",
                            "ID", "Name", "Category", "Issuer", "Renewal", "Cycle (months)");
                    System.out.println(
                            "---------------------------------------------------------------------------------------------");

                    for (Certificate cert : certs) {
                        String renewal = cert.isRequiresRenewal() ? "YES" : "NO";
                        String cycle = cert.getRenewalCycleMonths() != null
                                ? cert.getRenewalCycleMonths().toString()
                                : "-";

                        System.out.printf(
                                "%-3d | %-25s | %-12s | %-10s | %-10s | %-15s%n",
                                cert.getId(),
                                cert.getName(),
                                cert.getCategory(),
                                cert.getIssuerType(),
                                renewal,
                                cycle
                        );
                    }

                    System.out.println(
                            "---------------------------------------------------------------------------------------------");
                }
            }
            case 2 -> {
                System.out.print("Enter certificate ID: ");
                int id;
                try {
                    id = Integer.parseInt(inputScanner.nextLine().trim());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid ID. Please enter a whole number.");
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
                }, () -> System.out.println("Certificate with ID " + id + " not found."));
            }
            case 3 -> {
                System.out.print("New Certificate:\nPlease enter certificate name: ");
                String name = inputScanner.nextLine();

                System.out.print("Please enter category (e.g. Quality, Security, Regulatory): ");
                String category = inputScanner.nextLine();

                System.out.print("Please enter issuer type (e.g. ISO, IEC, Regulation, Audit): ");
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
                        System.out.println("Invalid number. Certificate creation cancelled.");
                        return;
                    }
                }

                System.out.print("Description (optional, press Enter to skip): ");
                String description = inputScanner.nextLine();

                try {
                    var created = certificateService.createCertificate(
                            name, category, issuerType, requiresRenewal, renewalCycleMonths, description
                    );
                    System.out.println("Certificate successfully created:");
                    System.out.println(created);
                } catch (RuntimeException e) {
                    System.out.println("Certificate creation failed: " + e.getMessage());
                }
            }
            case 4 -> {
                System.out.print("Enter certificate ID to change: ");
                int id;

                try {
                    id = Integer.parseInt(inputScanner.nextLine().trim());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid ID.");
                    return;
                }

                certificateService.findCertificateById(id).ifPresentOrElse(cert -> {

                    System.out.println("Press Enter to keep current value.");

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

                    certificateService.updateCertificate(cert);
                    System.out.println("Certificate updated successfully.");

                }, () -> System.out.println("Certificate not found."));
            }
            case 5 -> shouldExit = true;
        }
        if(shouldExit) startMainMenu();
        else certificatesMenu();
    }
}
