package de.htwsaar.communication.employee;

import de.htwsaar.application.EmployeeService;
import de.htwsaar.application.SkillService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;
import de.htwsaar.communication.employeeSkill.EmployeeSkillMenu;
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
            System.out.println("5. Delete Employee");
            System.out.println("6. Employee Skills");
            System.out.println("7. Return");

            switch (in.readMenuNumber(7)) {
                case 1 -> out.employeeTable(empService.findAllEmployees());
                case 2 -> EmployeeAddMenu.show(out, in, empService);
                case 3 -> EmployeeSearchMenu.show(out, in, empService);
                case 4 -> EmployeeChangeMenu.show(out, in, empService);
                case 5 -> EmployeeDeleteMenu.show(out, in, empService);
                case 6 -> new EmployeeSkillMenu(empService, skillService, in, out).show();
                case 7 -> back = true;
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
