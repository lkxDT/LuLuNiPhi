package de.htwsaar.communication.employee;

import de.htwsaar.application.EmployeeService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;

import java.util.List;

public class EmployeeDeleteMenu {

    private EmployeeDeleteMenu() {}

    public static void show(OutputRenderer out, InputReader in, EmployeeService empService) {

        System.out.print("Employee ID to delete: ");
        int id;
        try {
            id = Integer.parseInt(in.readLine().trim());
        } catch (NumberFormatException e) {
            out.error("Invalid employee ID.");
            return;
        }

        var empOpt = empService.findEmployeeByID(id);
        if (empOpt.isEmpty()) {
            out.error("Employee with ID " + id + " not found.");
            return;
        }

        out.info("You are about to permanently delete:");
        out.employeeTable(List.of(empOpt.get()));

        out.error("This action cannot be undone.");
        System.out.print("Type DELETE to confirm: ");
        String confirm = in.readLine().trim();

        if (!"DELETE".equals(confirm)) {
            out.info("Deletion cancelled.");
            return;
        }

        try {
            empService.deleteEmployeeById(id);
            out.info("Employee deleted successfully.");
        } catch (RuntimeException e) {
            out.error(e.getMessage());
        }
    }
}