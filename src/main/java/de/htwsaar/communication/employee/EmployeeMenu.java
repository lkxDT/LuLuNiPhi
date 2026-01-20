package de.htwsaar.communication.employee;
import de.htwsaar.communication.*;
import de.htwsaar.application.*;
import de.htwsaar.domain.Employee;

import java.util.List;

public class EmployeeMenu {

    private final EmployeeService empService;
    private final SkillService skillService;
    private final InputReader in;
    private final OutputRenderer out;

    public EmployeeMenu(
            EmployeeService empService,
            SkillService skillService,
            InputReader in,
            OutputRenderer out
    ) {
        this.empService = empService;
        this.skillService = skillService;
        this.in = in;
        this.out = out;
    }

    public void show() {
        out.section("Employees");
        System.out.println("1. Show all");
        System.out.println("2. Add");
        System.out.println("3. Search");
        System.out.println("4. Change");
        System.out.println("5. Employee Skills");
        System.out.println("6. Return");

        switch (in.readMenuNumber(6)) {
            case 1 -> out.employeeTable(empService.findAllEmployees());
            case 2 -> {
                out.section("Add Employee");

                System.out.print("First name: ");
                String firstName = in.readLine();

                System.out.print("Last name: ");
                String lastName = in.readLine();

                System.out.print("Role: ");
                String role = in.readLine();

                System.out.print("Status: ");
                String status = in.readLine();

                System.out.print("Hire date (yyyy-mm-dd): ");
                String hireDate = in.readLine();

                System.out.print("Working hours per week: ");
                int workingHours;
                try {
                    workingHours = Integer.parseInt(in.readLine().trim());
                } catch (NumberFormatException e) {
                    out.error("Invalid working hours.");
                    break;
                }

                try {
                    Employee created = empService.createEmployee(firstName, lastName, role, status, hireDate, workingHours);
                    out.info("Employee successfully created: " + created.getEmail());
                } catch (IllegalArgumentException e) {
                    // Eingabefehler (User)
                    out.error(e.getMessage());
                } catch (RuntimeException e) {
                    // Systemfehler (DB/Repo etc.)
                    out.error("Employee creation failed: " + e.getMessage());
                }
            }
            case 3 -> {
                this.showEmployeeSearchMenu();
            }
            case 4 -> {
                out.section("Change Employee");

                System.out.print("Enter employee ID to change: ");
                int id;
                try {
                    id = Integer.parseInt(in.readLine().trim());
                } catch (NumberFormatException e) {
                    out.error("Invalid ID. Please enter a whole number.");
                    break;
                }

                empService.findEmployeeByID(id).ifPresentOrElse(emp -> {

                    out.info("Press Enter to keep current value.");

                    System.out.print("First name [" + emp.getFirstName() + "]: ");
                    String firstName = in.readLine();
                    if (firstName.isBlank()) firstName = emp.getFirstName();

                    System.out.print("Last name  [" + emp.getLastName() + "]: ");
                    String lastName = in.readLine();
                    if (lastName.isBlank()) lastName = emp.getLastName();

                    System.out.print("Role       [" + emp.getRole() + "]: ");
                    String role = in.readLine();
                    if (role.isBlank()) role = emp.getRole();

                    System.out.print("Status     [" + emp.getStatus() + "]: ");
                    String status = in.readLine();
                    if (status.isBlank()) status = emp.getStatus();

                    System.out.print("Hire date  [" + emp.getHireDate() + "] (yyyy-mm-dd): ");
                    String hireDate = in.readLine();
                    if (hireDate.isBlank()) hireDate = emp.getHireDate();

                    int workingHours;
                    System.out.print("Working hours [" + emp.getWorkingHours() + "]: ");
                    String whStr = in.readLine();
                    if (whStr.isBlank()) {
                        workingHours = emp.getWorkingHours();
                    } else {
                        try {
                            workingHours = Integer.parseInt(whStr);
                        } catch (NumberFormatException e) {
                            out.error("Invalid working hours. Update cancelled.");
                            return;
                        }
                    }

                    String email = emp.getEmail();
                    if (!firstName.equals(emp.getFirstName()) || !lastName.equals(emp.getLastName())) {
                        System.out.print("Regenerate email based on the new name? (y/n): ");
                        String regen = in.readLine().trim().toLowerCase();
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
                        out.info("Employee updated successfully.");
                    } catch (RuntimeException e) {
                        out.error("Employee update failed: " + e.getMessage());
                    }

                }, () -> out.error("Employee with ID " + id + " not found."));
            }
            case 5 -> this.show();
        }
    }

    public void showEmployeeSearchMenu() {
        out.section("Search Employee");
        System.out.println("1. By ID");
        System.out.println("2. By last name");
        System.out.println("3. Return to employees");

        switch (in.readMenuNumber(3)) {
            case 1 -> {
                System.out.print("Enter ID: ");
                int id;
                try {
                    id = Integer.parseInt(in.readLine().trim());
                } catch (NumberFormatException e) {
                    out.error("Invalid ID.");
                    return;
                }

                empService.findEmployeeByID(id).ifPresentOrElse(
                        employee -> out.employeeTable(List.of(employee)),
                        () -> out.error("Employee with ID " + id + " not found.")
                );
            }

            case 2 -> {
                System.out.print("Enter last name (or part of it): ");
                String lastName = in.readLine().trim();
                out.employeeTable(empService.findEmployeesByLastName(lastName));
            }

            case 3 -> this.show();
        }
    }
}