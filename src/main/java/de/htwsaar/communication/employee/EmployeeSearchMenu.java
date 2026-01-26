package de.htwsaar.communication.employee;

import de.htwsaar.application.EmployeeService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;

import java.util.List;

/**
 * Menu for searching employees.
 * <p>
 * Provides options to search employees either by ID or by last name.
 * The menu is static and does not maintain state.
 */
public class EmployeeSearchMenu {
    /**
     * Private constructor to prevent instantiation.
     */
    private EmployeeSearchMenu() {}
    /**
     * Displays the search menu and handles user input for searching employees.
     * <p>
     * Options:
     * <ul>
     *     <li>Search by employee ID</li>
     *     <li>Search by employee last name</li>
     *     <li>Return to the previous menu</li>
     * </ul>
     *
     * @param out        OutputRenderer for displaying information
     * @param in         InputReader for reading user input
     * @param empService EmployeeService used to retrieve employee data
     */
    protected static void show(OutputRenderer out, InputReader in, EmployeeService empService) {
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
}
