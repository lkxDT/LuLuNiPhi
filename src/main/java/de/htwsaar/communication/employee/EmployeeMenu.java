package de.htwsaar.communication.employee;

import de.htwsaar.application.EmployeeService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;

/**
 * Main menu for managing employees.
 * <p>
 * Provides options to show all employees, search, add, change, or delete employees.
 * Loops until the user chooses to return.
 */
public class EmployeeMenu {

    private final EmployeeService empService;
    private final InputReader in;
    private final OutputRenderer out;

    /**
     * Constructs an EmployeeMenu with the required services and I/O handlers.
     *
     * @param empService Service for employee operations
     * @param in         InputReader for user input
     * @param out        OutputRenderer for console output
     */
    public EmployeeMenu(EmployeeService empService, InputReader in, OutputRenderer out) {
        this.empService = empService;
        this.in = in;
        this.out = out;
    }

    /**
     * Displays the employee menu and handles user interactions.
     * <p>
     * Users can:
     * <ul>
     *     <li>Show all employees</li>
     *     <li>Search for a specific employee</li>
     *     <li>Add a new employee</li>
     *     <li>Change an existing employee</li>
     *     <li>Delete an employee</li>
     *     <li>Return to the previous menu</li>
     * </ul>
     */
    public void show() {
        boolean back = false;

        while (!back) {
            out.section("Employees");
            System.out.println("1. Show All");
            System.out.println("2. Search Employee");
            System.out.println("3. Add Employee");
            System.out.println("4. Change Employee");
            System.out.println("5. Delete Employee");
            System.out.println("6. Return");

            switch (in.readMenuNumber(6)) {
                case 1 -> out.employeeTable(empService.findAllEmployees());
                case 2 -> EmployeeSearchMenu.show(out, in, empService);
                case 3 -> EmployeeAddMenu.show(out, in, empService);
                case 4 -> EmployeeChangeMenu.show(out, in, empService);
                case 5 -> EmployeeDeleteMenu.show(out, in, empService);
                case 6 -> back = true;
            }
        }
    }
}