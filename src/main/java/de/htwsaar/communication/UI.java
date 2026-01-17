
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

    /**
     * Starts the main menu
     */
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

    /**
     * Opens the Menu for Employee
     */
    public void employeeMenu() {
        System.out.println("Employees Menu:\n" +
                "1. Show all\n" +
                "2. Add\n" +
                "3. Search\n" +
                "4. Change\n" +
                "5. Return to main\n");

        switch (this.getInputNumber(5)) {
            case 1 -> {
                for (Employee emp : empService.findAllEmployees()) {
                    System.out.println(emp);
                }
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
            case 4 -> System.out.println("Placeholder:");
            case 5 -> shouldExit = true;
        }
        if(shouldExit) startMainMenu();
        else employeeMenu();
    }

    public void employeeSearchMenu() {
        System.out.println("Search Employee:\n" +
                "1. By ID\n" +
                "2. Return to employee\n");

        switch (this.getInputNumber(2)) {
            case 1 -> {
                System.out.println("Enter ID:\n");
                int id = inputScanner.nextInt();
                empService.findEmployeeByID(id).ifPresentOrElse(
                        employee -> System.out.println(employee.toString()),
                        () -> System.out.printf("Employee with ID %d not found\n", id)
                );
            }
            case 2 -> employeeMenu();
        }
    }

    /**
     * Opens the Menu for Skill
     */
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
            case 2 -> System.out.println("placeholder: add skill");
            case 3 -> skillSearchMenu();
            case 4 -> System.out.println("placeholder: change skill");
            case 5 -> shouldExit = true;
        }
        if(shouldExit) startMainMenu();
        else skillsMenu();
    }

    public void skillSearchMenu() {
        System.out.println("Search Skill:\n" +
                "1. By ID\n" +
                "2. By name\n" +
                "2. Return to employee\n");

        switch (this.getInputNumber(2)) {
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
                "2. Add\n" +
                "3. Change\n" +
                "4. Return to main \n");

        switch (this.getInputNumber(4)) {
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
            case 2 -> System.out.println("placeholder: add certificate");
            case 3 -> System.out.println("placeholder: change certificate");
            case 4 -> shouldExit = true;
        }
        if(shouldExit) startMainMenu();
        else certificatesMenu();
    }
}
