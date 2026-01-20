package de.htwsaar.communication.employee;

import java.util.List;

import de.htwsaar.application.EmployeeService;
import de.htwsaar.application.SkillService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;
import de.htwsaar.domain.Employee;

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
        boolean back = false;

        while (!back) {
            out.section("Employees");
            System.out.println("1. Show all");
            System.out.println("2. Add");
            System.out.println("3. Search");
            System.out.println("4. Change");
            System.out.println("5. Employee Skills");
            System.out.println("6. Return");

            switch (in.readMenuNumber(6)) {
                case 1 -> out.employeeTable(empService.findAllEmployees());
                case 2 -> addEmployee();
                case 3 -> searchEmployee();
                case 4 -> changeEmployee();
                case 5 -> new EmployeeSkillMenu(empService, skillService, in, out).show();
                case 6 -> back = true;
            }
        }
    }

    private void addEmployee() {
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
        int hours;
        try {
            hours = in.readInt("Invalid working hours.");
        } catch (IllegalArgumentException e) {
            out.error(e.getMessage());
            return;
        }

        try {
            Employee created = empService.createEmployee(
                    firstName, lastName, role, status, hireDate, hours
            );
            out.info("Employee created: " + created.getEmail());
        } catch (RuntimeException e) {
            out.error(e.getMessage());
        }
    }

    private void searchEmployee() {
        out.section("Search Employee");
        System.out.println("1. By ID");
        System.out.println("2. By last name");
        System.out.println("3. Return");

        switch (in.readMenuNumber(3)) {
            case 1 -> {
                System.out.print("Enter ID: ");
                try {
                    int id = in.readInt("Invalid ID.");
                    empService.findEmployeeByID(id)
                            .ifPresentOrElse(
                                    e -> out.employeeTable(List.of(e)),
                                    () -> out.error("Employee not found.")
                            );
                } catch (IllegalArgumentException e) {
                    out.error(e.getMessage());
                }
            }
            case 2 -> {
                System.out.print("Enter last name: ");
                String name = in.readLine();
                out.employeeTable(empService.findEmployeesByLastName(name));
            }
        }
    }

    private void changeEmployee() {
        out.section("Change Employee");

        System.out.print("Employee ID: ");
        int id;
        try {
            id = in.readInt("Invalid ID.");
        } catch (IllegalArgumentException e) {
            out.error(e.getMessage());
            return;
        }

        empService.findEmployeeByID(id).ifPresentOrElse(emp -> {

            System.out.println("Press Enter to keep current value.");

            System.out.print("First name [" + emp.getFirstName() + "]: ");
            String fn = in.readLine();
            if (fn.isBlank()) fn = emp.getFirstName();

            System.out.print("Last name [" + emp.getLastName() + "]: ");
            String ln = in.readLine();
            if (ln.isBlank()) ln = emp.getLastName();

            System.out.print("Role [" + emp.getRole() + "]: ");
            String role = in.readLine();
            if (role.isBlank()) role = emp.getRole();

            System.out.print("Status [" + emp.getStatus() + "]: ");
            String status = in.readLine();
            if (status.isBlank()) status = emp.getStatus();

            System.out.print("Hire date [" + emp.getHireDate() + "]: ");
            String hire = in.readLine();
            if (hire.isBlank()) hire = emp.getHireDate();

            System.out.print("Working hours [" + emp.getWorkingHours() + "]: ");
            String wh = in.readLine();
            int hours = emp.getWorkingHours();
            if (!wh.isBlank()) {
                try {
                    hours = Integer.parseInt(wh);
                } catch (NumberFormatException e) {
                    out.error("Invalid working hours.");
                    return;
                }
            }

            String email = emp.getEmail();
            if (!fn.equals(emp.getFirstName()) || !ln.equals(emp.getLastName())) {
                System.out.print("Regenerate email? (y/n): ");
                String regen = in.readLine().trim().toLowerCase();
                if (regen.equals("y") || regen.equals("yes")) {
                    email = empService.generateEmailForUpdate(fn, ln, emp.getId());
                }
            }

            Employee updated = new Employee(
                    emp.getId(), fn, ln, email, role, status, hire, hours
            );

            try {
                empService.updateEmployee(updated);
                out.info("Employee updated.");
            } catch (RuntimeException e) {
                out.error(e.getMessage());
            }

        }, () -> out.error("Employee not found."));
    }
}
