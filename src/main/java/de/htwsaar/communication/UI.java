
package de.htwsaar.communication;

import java.util.Scanner;
import de.htwsaar.application.*;
import de.htwsaar.domain.Employee;
import de.htwsaar.domain.Skill;

public class UI {
    private final Scanner inputScanner;
    private final EmployeeService empService;
    private final SkillService skillService;

    //Standat Scanner for usage
    public UI(EmployeeService ems, SkillService sks) {
        this(new Scanner(System.in), ems, sks);
    }

    /**
     * Constructor for the UI Object
     * @param inputScanner Scanner for Input (for tests)
     * @param ems EmployeeService abstraction
     * @param sks SkillService abstraction
     */
    public UI(Scanner inputScanner, EmployeeService ems, SkillService sks) {
        this.empService = ems;
        this.skillService = sks;
        this.inputScanner = inputScanner;
    }

    public int getInputNumber(int max) {
        while (true) {
            if (!inputScanner.hasNext()) {
                return -1;
            }

            String input = inputScanner.next();

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
        System.out.println("Main Menu:\n" +
                "1. Employees\n" +
                "2. Skills\n" +
                "3. Certificates\n" +
                "4. Projects");

        try {
            switch (this.getInputNumber(4)) {
                case 1 -> employeeMenu();
                case 2 -> skillsMenu();
                case 3 -> certificatesMenu();
                case 4 -> projectsMenu();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            startMainMenu();
        }
    }

    /**
     * Opens the Menu for Employee
     */
    public void employeeMenu() {
        System.out.println("Employees Menu:\n" +
                "1. show all\n" +
                "2. add\n" +
                "3. change\n");

        switch (this.getInputNumber(3)) {
            case 1 -> {
                for(Employee emp : empService.getAllEmployees()) {
                    System.out.println(emp);
                }
            }
            case 2 -> {
                String firstName, lastName, email, role, status, hireDate;
                int workingHours;

                System.out.print("New Employee: \nPlease enter firstname: ");
                firstName = inputScanner.next();
                System.out.print("New Employee: \nPlease enter lastname: ");
                lastName = inputScanner.next();
                System.out.print("New Employee: \nPlease enter mail address: ");
                email = inputScanner.next();
                System.out.print("New Employee: \nPlease enter a role: ");
                role = inputScanner.next();
                System.out.print("New Employee: \nPlease enter a status for the employee: ");
                status = inputScanner.next();
                System.out.print("New Employee: \nPlease enter the date the employee was hired (yyyy-mm-dd): ");
                hireDate = inputScanner.next();
                System.out.print("New Employee: \nPlease enter the amount of hours the employee works in a week: ");
                workingHours = inputScanner.nextInt();

                try {
                    empService.createEmployee(firstName, lastName, email, role, status, hireDate, workingHours);
                    System.out.println("Employee successfully");
                } catch (RuntimeException e) {
                    System.out.println("Employee creation failed");
                }

            }
            case 3 -> System.out.println("placeholder: change employee");
        }
    }
    /**
     * Opens the Menu for Skill
     */
    public void skillsMenu() {
        System.out.println("Skills Menu:\n" +
                "1. show all\n" +
                "2. add\n" +
                "3. change\n");

        switch (this.getInputNumber(3)) {
            case 1 -> {
                for(Skill skill : skillService.getAllSkills()) {
                    System.out.println(skill);
                }
            }
            case 2 -> System.out.println("placeholder: add skill");
            case 3 -> System.out.println("placeholder: change skill");
        }
    }

    public void certificatesMenu() {
        System.out.println("Certificates Menu:\n" +
                "1. show all\n" +
                "2. add\n" +
                "3. change\n");

        switch (this.getInputNumber(3)) {
            case 1 -> System.out.println("placeholder: show all certificates");
            case 2 -> System.out.println("placeholder: add certificate");
            case 3 -> System.out.println("placeholder: change certificate");
        }
    }

    public void projectsMenu() {
        System.out.println("Projects Menu:\n" +
                "1. show all\n" +
                "2. new project\n" +
                "3. change employees\n" +
                "4. change needed skills");

        // Fixed: Adjusted max to 4 to match the options printed above
        switch (this.getInputNumber(4)) {
            case 1 -> System.out.println("placeholder: show all projects");
            case 2 -> newProjectMenu();
            case 3 -> System.out.println("placeholder: change employees");
            case 4 -> System.out.println("placeholder: change skills");
        }
    }

    public void newProjectMenu() {
        System.out.println("employee selection for new Project:\n" +
                "1. manual\n" +
                "2. automatic");

        switch (this.getInputNumber(2)) {
            case 1 -> System.out.println("placeholder: manual selection");
            case 2 -> System.out.println("placeholder: automatic selection");
        }
    }
}
