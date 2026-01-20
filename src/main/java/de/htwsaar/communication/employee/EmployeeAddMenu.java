package de.htwsaar.communication.employee;

import de.htwsaar.application.EmployeeService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;
import de.htwsaar.domain.Employee;


public class EmployeeAddMenu {
    private EmployeeAddMenu() {}
    protected static void show(OutputRenderer out,
                                    InputReader in,
                                    EmployeeService empService
    ) {
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
}
