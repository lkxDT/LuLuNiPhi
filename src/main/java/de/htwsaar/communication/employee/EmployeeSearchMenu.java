package de.htwsaar.communication.employee;

import de.htwsaar.application.EmployeeService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;

import java.util.List;

public class EmployeeSearchMenu {
    private EmployeeSearchMenu() {}
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
