package de.htwsaar.communication.employee;

import de.htwsaar.application.EmployeeService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;

public class EmployeeMenu {

    private final EmployeeService empService;
    private final InputReader in;
    private final OutputRenderer out;

    public EmployeeMenu(EmployeeService empService, InputReader in, OutputRenderer out) {
        this.empService = empService;
        this.in = in;
        this.out = out;
    }

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