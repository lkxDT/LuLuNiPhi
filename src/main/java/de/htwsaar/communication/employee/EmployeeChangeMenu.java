package de.htwsaar.communication.employee;

import de.htwsaar.application.EmployeeService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;
import de.htwsaar.domain.Employee;

/**
 * Menu for updating an existing employee's information.
 */
public class EmployeeChangeMenu {
    private EmployeeChangeMenu() {}

    /**
     * Displays the menu to update an employee's details.
     * Allows editing first name, last name, role, status, hire date, and working hours.
     * Optionally regenerates the email if the name changes.
     *
     * @param out        OutputRenderer for console output
     * @param in         InputReader for user input
     * @param empService Service for employee operations
     */
    protected static void show(OutputRenderer out, InputReader in, EmployeeService empService) {
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
